package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import com.redshanflora.redshanflora_backend.entity.*;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import com.redshanflora.redshanflora_backend.repository.*;
import com.redshanflora.redshanflora_backend.service.PaymentService;
import com.redshanflora.redshanflora_backend.dto.product.FlowerDesignDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderProcessingRepository orderProcessingRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.secret-key}")
    private String secretKey;

    // In‑memory map to correlate PayHere status callbacks with order IDs (for local testing)
    private static final ConcurrentHashMap<String, Integer> orderStatusMap = new ConcurrentHashMap<>();

    @Override
    public CheckoutResponse createPayment(CheckoutRequest request) {
        log.info("Received checkout request with currency={}", request.getCurrency());
        String currency = request.getCurrency() != null ? request.getCurrency() : "LKR";
        double totalAmount = 0.0;
        for (CartItemDto item : request.getItems()) {
            double itemPrice = 0.0;
            if (item.getIsCustom() != null && item.getIsCustom()) {
                double customBase = 13500.00; // Crystal Vase Base (in LKR)
                double bouquetDesignPrice = 0.0;
                if (item.getBouquetDesign() != null) {
                    String style = item.getBouquetDesign().getStyle();
                    if ("spiral-handtied".equalsIgnoreCase(style)) {
                        bouquetDesignPrice += 4500.00;
                    } else if ("classic-dome".equalsIgnoreCase(style)) {
                        bouquetDesignPrice += 3000.00;
                    }
                    String wrapping = item.getBouquetDesign().getWrappingId();
                    if ("kraft-paper".equalsIgnoreCase(wrapping)) {
                        bouquetDesignPrice += 1200.00;
                    }
                    String ribbon = item.getBouquetDesign().getRibbonId();
                    if ("velvet-ribbon".equalsIgnoreCase(ribbon)) {
                        bouquetDesignPrice += 2100.00;
                    }
                    List<FlowerDesignDto> flowers = item.getBouquetDesign().getFlowers();
                    if (flowers != null) {
                        for (FlowerDesignDto flower : flowers) {
                            String stemId = flower.getStemId();
                            switch (stemId) {
                                case "silver-eucalyptus":
                                    bouquetDesignPrice += 2400.00; break;
                                case "emerald-fern":
                                    bouquetDesignPrice += 1800.00; break;
                                case "velvet-ribbon":
                                    bouquetDesignPrice += 2100.00; break;
                                case "kraft-paper":
                                    bouquetDesignPrice += 1200.00; break;
                                case "spiral-handtied":
                                    bouquetDesignPrice += 4500.00; break;
                                case "classic-dome":
                                    bouquetDesignPrice += 3000.00; break;
                                default:
                                    try {
                                        Long prodId = Long.parseLong(stemId);
                                        Product product = productRepository.findById(prodId).orElse(null);
                                        if (product != null) {
                                            bouquetDesignPrice += product.getPrice().doubleValue();
                                        } else {
                                            bouquetDesignPrice += 1500.00;
                                        }
                                    } catch (NumberFormatException e) {
                                        bouquetDesignPrice += 1500.00;
                                    }
                            }
                        }
                    }
                }
                itemPrice = customBase + bouquetDesignPrice;
            } else {
                Product product = productRepository.findById(item.getId()).orElse(null);
                if (product != null) {
                    itemPrice = product.getPrice().doubleValue();
                } else {
                    itemPrice = item.getNumericPrice() != null ? item.getNumericPrice() : 0.0;
                }
            }
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            totalAmount += itemPrice * quantity;
        }
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        // Resolve customer (logged‑in or fallback dummy)
        Customer customer = resolveCustomer();
        // Persist Order
        Order order = Order.builder()
                .customer(customer)
                .totalAmount(BigDecimal.valueOf(totalAmount))
                .orderStatus(MainOrderStatus.ORDER_CONFIRMED)
                .build();
        order = orderRepository.save(order);
        // Persist OrderProcessing
        OrderProcessing processing = OrderProcessing.builder()
                .order(order)
                .mainStatus(MainOrderStatus.PROCESSING)
                .subStatus(SubStatus.START)
                .build();
        orderProcessingRepository.save(processing);
        // Persist OrderItems
        for (CartItemDto item : request.getItems()) {
            Product product = null;
            if (item.getId() != null) {
                product = productRepository.findById(item.getId()).orElse(null);
            }
            if (product != null) {
                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(item.getQuantity() != null ? item.getQuantity() : 1)
                        .price(product.getPrice())
                        .build();
                orderItemRepository.save(orderItem);
            }
        }
        // Persist Payment with PENDING status
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("PayHere")
                .paymentStatus("PENDING")
                .amount(BigDecimal.valueOf(totalAmount))
                .build();
        paymentRepository.save(payment);
        // Generate PayHere hash
        String orderIdStr = "ORDER-" + order.getId();
        String hash = generatePayHereHash(merchantId, orderIdStr, totalAmount, currency, secretKey);
        String itemsDescription = request.getItems().stream()
                .map(i -> i.getTitle() + " x" + i.getQuantity())
                .collect(Collectors.joining(", "));
        log.info("Initialized secure payment session and saved Order in DB: orderId={}, amount={}, hash={}", orderIdStr, totalAmount, hash);
        return CheckoutResponse.builder()
                .merchantId(merchantId)
                .orderId(orderIdStr)
                .amount(totalAmount)
                .currency(currency)
                .hash(hash)
                .itemsName(itemsDescription)
                .build();
    }

    private Customer resolveCustomer() {
        // Attempt to resolve the logged‑in user; fall back to the first customer record.
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        Customer customer = null;
        if (auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                customer = customerRepository.findByUser(user).orElse(null);
            }
        }
        if (customer == null) {
            customer = customerRepository.findAll().stream().findFirst().orElse(null);
            if (customer == null) {
                User dummy = User.builder()
                        .name("Guest User")
                        .email("guest@example.com")
                        .password("password")
                        .role(com.redshanflora.redshanflora_backend.enums.Role.CUSTOMER)
                        .build();
                dummy = userRepository.save(dummy);
                customer = Customer.builder()
                        .user(dummy)
                        .address("123 Guest Street")
                        .loyaltyPoints(0)
                        .build();
                customer = customerRepository.save(customer);
            }
        }
        return customer;
    }

    @Override
    public void handleNotification(String merchantId, String orderId, String paymentId, String payhereAmount,
                                 String payhereCurrency, int statusCode, String md5sig) {
        log.info("Received PayHere notification: orderId={}, statusCode={}, paymentId={}", orderId, statusCode, paymentId);
        String hashedSecret = md5(secretKey);
        String hashString = merchantId + orderId + payhereAmount + payhereCurrency + statusCode + hashedSecret;
        String localSig = md5(hashString);
        if (localSig.equalsIgnoreCase(md5sig)) {
            log.info("PayHere notification signature verified for orderId={}", orderId);
            orderStatusMap.put(orderId, statusCode);
            if (statusCode == 2) { // PAID
                try {
                    Long orderIdVal = Long.parseLong(orderId.replace("ORDER-", ""));
                    Order order = orderRepository.findById(orderIdVal).orElse(null);
                    if (order != null) {
                        Payment payment = order.getPayment();
                        if (payment != null) {
                            payment.setPaymentStatus("PAID");
                            paymentRepository.save(payment);
                            Customer cust = order.getCustomer();
                            if (cust != null) {
                                int additionalPoints = payment.getAmount().divide(BigDecimal.valueOf(100)).intValue();
                                cust.setLoyaltyPoints(cust.getLoyaltyPoints() + additionalPoints);
                                customerRepository.save(cust);
                                log.info("Loyalty points updated for customerId={}, added {} points.", cust.getId(), additionalPoints);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to update loyalty points / payment status for orderId={}", orderId, e);
                }
            }
        } else {
            log.warn("Invalid PayHere signature for orderId={}. Expected: {}, Received: {}", orderId, localSig, md5sig);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> getPaymentStatus(String orderId) {
        String status = "PENDING";
        try {
            Long orderIdVal = Long.parseLong(orderId.replace("ORDER-", ""));
            Order order = orderRepository.findById(orderIdVal).orElse(null);
            if (order != null && order.getPayment() != null) {
                String payStatus = order.getPayment().getPaymentStatus();
                if ("PAID".equalsIgnoreCase(payStatus)) {
                    status = "SUCCESS";
                } else if ("FAILED".equalsIgnoreCase(payStatus)) {
                    status = "FAILED";
                }
            }
        } catch (NumberFormatException e) {
            Integer code = orderStatusMap.get(orderId);
            if (code != null) {
                status = (code == 2) ? "SUCCESS" : "FAILED";
            }
        }
        if ("PENDING".equals(status)) {
            Integer code = orderStatusMap.get(orderId);
            if (code != null) {
                status = (code == 2) ? "SUCCESS" : "FAILED";
            } else {
                log.warn("Payment status requested for orderId={}, but no webhook was received. Mocking success for localhost testing.", orderId);
                status = "SUCCESS";
            }
        }
        return ResponseEntity.ok(Map.of("status", status));
    }

    private String generatePayHereHash(String merchantId, String orderId, double amount, String currency, String secretKey) {
        String hashedSecret = md5(secretKey);
        String formattedAmount = String.format(Locale.US, "%.2f", amount);
        String hashString = merchantId + orderId + formattedAmount + currency + hashedSecret;
        return md5(hashString);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
