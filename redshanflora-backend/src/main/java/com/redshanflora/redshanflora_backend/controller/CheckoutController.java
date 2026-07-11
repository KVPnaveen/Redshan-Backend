package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.product.FlowerDesignDto;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import com.redshanflora.redshanflora_backend.entity.OrderProcessing;
import com.redshanflora.redshanflora_backend.entity.Payment;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.enums.Role;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.OrderItemRepository;
import com.redshanflora.redshanflora_backend.repository.OrderProcessingRepository;
import com.redshanflora.redshanflora_backend.repository.OrderRepository;
import com.redshanflora.redshanflora_backend.repository.PaymentRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class CheckoutController {

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

    private static final ConcurrentHashMap<String, Integer> orderStatusMap = new ConcurrentHashMap<>();

    @PostMapping("/api/checkout")
    public ResponseEntity<CheckoutResponse> initializeCheckout(@RequestBody CheckoutRequest request) {
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
                            if ("silver-eucalyptus".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 2400.00;
                            } else if ("emerald-fern".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 1800.00;
                            } else if ("velvet-ribbon".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 2100.00;
                            } else if ("kraft-paper".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 1200.00;
                            } else if ("spiral-handtied".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 4500.00;
                            } else if ("classic-dome".equalsIgnoreCase(stemId)) {
                                bouquetDesignPrice += 3000.00;
                            } else {
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

        double discount = request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0;
        totalAmount = Math.max(0.0, totalAmount - discount);
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        
        // 1. Resolve logged-in Customer using findByUserId
        Customer customer = null;
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                customer = customerRepository.findByUserId(user.getId()).orElse(null);
                if (customer == null) {
                    customer = Customer.builder()
                            .user(user)
                            .address(user.getAddress() != null ? user.getAddress() : "Default Address")
                            .loyaltyPoints(0)
                            .build();
                    customer = customerRepository.save(customer);
                }
            }
        }
        if (customer == null) {
            throw new com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException("User is not authenticated or customer record is missing.");
        }

        // 2. Persist Order in database
        Order order = Order.builder()
                .customer(customer)
                .totalAmount(BigDecimal.valueOf(totalAmount))
                .orderStatus(MainOrderStatus.ORDER_CONFIRMED)
                .build();
        order = orderRepository.save(order);

        // 3. Persist OrderProcessing in database
        OrderProcessing processing = OrderProcessing.builder()
                .order(order)
                .mainStatus(MainOrderStatus.PROCESSING)
                .subStatus(SubStatus.START)
                .build();
        orderProcessingRepository.save(processing);

        // 4. Persist OrderItems in database
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

        // 5. Persist Payment with PENDING status in database
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("PayHere")
                .paymentStatus("PENDING")
                .amount(BigDecimal.valueOf(totalAmount))
                .build();
        paymentRepository.save(payment);

        String orderIdStr = "ORDER-" + order.getId();
        String hash = generatePayHereHash(merchantId, orderIdStr, totalAmount, currency, secretKey);
        
        String itemsDescription = request.getItems().stream()
                .map(item -> item.getTitle() + " x" + item.getQuantity())
                .collect(Collectors.joining(", "));

        log.info("Initialized secure payment session and saved Order in DB: orderId={}, amount={}, hash={}", orderIdStr, totalAmount, hash);

        CheckoutResponse response = CheckoutResponse.builder()
                .merchantId(merchantId)
                .orderId(orderIdStr)
                .amount(totalAmount)
                .currency(currency)
                .hash(hash)
                .itemsName(itemsDescription)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = {"/api/checkout/notify", "/api/payment/notify"})
    public ResponseEntity<Void> receiveNotification(
            @RequestParam("merchant_id") String merchantId,
            @RequestParam("order_id") String orderId,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("payhere_amount") String payhereAmount,
            @RequestParam("payhere_currency") String payhereCurrency,
            @RequestParam("status_code") int statusCode,
            @RequestParam("md5sig") String md5sig) {
        
        log.info("Received PayHere notification: orderId={}, statusCode={}, paymentId={}", orderId, statusCode, paymentId);
        
        String hashedSecret = md5(secretKey);
        String hashString = merchantId + orderId + payhereAmount + payhereCurrency + statusCode + hashedSecret;
        String localSig = md5(hashString);
        
        if (localSig.equalsIgnoreCase(md5sig)) {
            log.info("PayHere notification signature verified for orderId={}", orderId);
            orderStatusMap.put(orderId, statusCode);
            
            // Webhook Loyalty Points Logic
            if (statusCode == 2) {
                try {
                    Long orderIdVal = Long.parseLong(orderId.replace("ORDER-", ""));
                    Order order = orderRepository.findById(orderIdVal).orElse(null);
                    if (order != null) {
                        Payment payment = order.getPayment();
                        if (payment != null) {
                            payment.setPaymentStatus("PAID");
                            paymentRepository.save(payment);
                            
                            Customer customer = order.getCustomer();
                            if (customer != null) {
                                int additionalPoints = payment.getAmount().divide(BigDecimal.valueOf(100)).intValue();
                                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + additionalPoints);
                                customerRepository.save(customer);
                                log.info("Loyalty points updated for customerId={}. Added {} points.", customer.getId(), additionalPoints);
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
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/checkout/status/{orderId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String orderId) {
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
            Integer statusCode = orderStatusMap.get(orderId);
            if (statusCode != null) {
                if (statusCode == 2) {
                    status = "SUCCESS";
                } else {
                    status = "FAILED";
                }
            }
        }
        
        if ("PENDING".equals(status)) {
            Integer statusCode = orderStatusMap.get(orderId);
            if (statusCode != null) {
                if (statusCode == 2) {
                    status = "SUCCESS";
                } else {
                    status = "FAILED";
                }
            } else {
                log.warn("Payment status requested for orderId={}, but no webhook was received. Mocking success for localhost testing.", orderId);
                status = "SUCCESS";
                try {
                    Long orderIdVal = Long.parseLong(orderId.replace("ORDER-", ""));
                    Order order = orderRepository.findById(orderIdVal).orElse(null);
                    if (order != null) {
                        Payment payment = order.getPayment();
                        if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {
                            payment.setPaymentStatus("PAID");
                            paymentRepository.save(payment);
                            
                            Customer customer = order.getCustomer();
                            if (customer != null) {
                                int additionalPoints = payment.getAmount().divide(BigDecimal.valueOf(100)).intValue();
                                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + additionalPoints);
                                customerRepository.save(customer);
                                log.info("Mocked success: Loyalty points updated for customerId={}. Added {} points.", customer.getId(), additionalPoints);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to update loyalty points for mocked success, orderId={}", orderId, e);
                }
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
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
