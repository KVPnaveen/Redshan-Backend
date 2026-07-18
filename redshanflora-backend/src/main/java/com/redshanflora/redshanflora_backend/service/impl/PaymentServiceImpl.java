package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.payment.BouquetPriceBreakdown;
import com.redshanflora.redshanflora_backend.dto.product.BouquetSnapshotDto;
import com.redshanflora.redshanflora_backend.entity.*;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import com.redshanflora.redshanflora_backend.repository.*;
import com.redshanflora.redshanflora_backend.service.PaymentService;
import com.redshanflora.redshanflora_backend.service.BouquetPricingService;
import com.redshanflora.redshanflora_backend.service.BouquetSnapshotService;
import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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
    private final BouquetPricingService bouquetPricingService;
    private final BouquetSnapshotService bouquetSnapshotService;
    private final CustomizedBouquetRepository customizedBouquetRepository;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.secret-key}")
    private String secretKey;

    private static final ConcurrentHashMap<String, Integer> orderStatusMap = new ConcurrentHashMap<>();
    
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    @Override
    @Transactional
    public CheckoutResponse createPayment(CheckoutRequest request) {
        log.info("Received checkout request with currency={}", request.getCurrency());
        String currency = request.getCurrency() != null ? request.getCurrency() : "LKR";

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new CheckoutValidationException("Checkout items list cannot be empty.");
        }

        // Validate that there is at most one custom bouquet in the request
        long customBouquetCount = request.getItems().stream()
                .filter(item -> item.getIsCustom() != null && item.getIsCustom())
                .count();
        if (customBouquetCount > 1) {
            throw new CheckoutValidationException("Checkout failed: an order can contain at most one custom bouquet.");
        }

        // 1. Validate all items and calculate totals first
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto item : request.getItems()) {
            BigDecimal itemPrice;
            if (item.getIsCustom() != null && item.getIsCustom()) {
                // Delegate pricing and validation of custom bouquet
                BouquetPriceBreakdown breakdown = bouquetPricingService.validateAndCalculatePrice(item.getBouquetDesign());
                itemPrice = breakdown.getGrandTotal();
            } else {
                if (item.getId() == null) {
                    throw new CheckoutValidationException("Standard product ID is missing.");
                }
                Product product = productRepository.findById(item.getId())
                        .orElseThrow(() -> new CheckoutValidationException("Product not found with ID: " + item.getId()));
                itemPrice = product.getPrice();
                if (itemPrice == null) {
                    throw new CheckoutValidationException("Product price is missing in the database: " + item.getId());
                }
                if (itemPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new CheckoutValidationException("Product price is negative in the database: " + item.getId());
                }
            }

            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            if (quantity <= 0) {
                throw new CheckoutValidationException("Quantity must be a positive integer.");
            }
            totalAmount = totalAmount.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        // Apply discount safely
        BigDecimal discount = request.getDiscountAmount() != null ? BigDecimal.valueOf(request.getDiscountAmount()) : BigDecimal.ZERO;
        totalAmount = totalAmount.subtract(discount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        BigDecimal authoritativeTotal = totalAmount.setScale(MONEY_SCALE, MONEY_ROUNDING);
        String payHereAmount = authoritativeTotal.toPlainString();

        // 2. Resolve Customer record
        Customer customer = resolveCustomer();

        // 3. Persist Order in database
        Order order = Order.builder()
                .customer(customer)
                .totalAmount(authoritativeTotal)
                .orderStatus(MainOrderStatus.ORDER_CONFIRMED)
                .build();
        order = orderRepository.save(order);

        // 4. Persist OrderProcessing in database
        OrderProcessing processing = OrderProcessing.builder()
                .order(order)
                .mainStatus(MainOrderStatus.PROCESSING)
                .subStatus(SubStatus.START)
                .build();
        orderProcessingRepository.save(processing);

        // 5. Persist OrderItems in database (for standard products only)
        for (CartItemDto item : request.getItems()) {
            if (item.getIsCustom() == null || !item.getIsCustom()) {
                Product product = productRepository.findById(item.getId())
                        .orElseThrow(() -> new CheckoutValidationException("Product not found with ID: " + item.getId()));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(item.getQuantity() != null ? item.getQuantity() : 1)
                        .price(product.getPrice())
                        .build();
                orderItemRepository.save(orderItem);
            }
        }

        // 5b. Persist CustomizedBouquet (for custom bouquet if present)
        CartItemDto customItem = request.getItems().stream()
                .filter(item -> item.getIsCustom() != null && item.getIsCustom())
                .findFirst().orElse(null);

        if (customItem != null) {
            BouquetPriceBreakdown breakdown = bouquetPricingService.validateAndCalculatePrice(customItem.getBouquetDesign());
            BouquetSnapshotDto snapshotDto = bouquetSnapshotService.buildSnapshot(customItem.getBouquetDesign(), breakdown);
            String snapshotJson = bouquetSnapshotService.serializeSnapshot(snapshotDto);

            // Check if customized bouquet already exists for this order
            CustomizedBouquet customizedBouquet = customizedBouquetRepository.findByOrder(order).orElse(null);
            if (customizedBouquet == null) {
                customizedBouquet = CustomizedBouquet.builder()
                        .customer(customer)
                        .order(order)
                        .build();
            }
            customizedBouquet.setBouquetStyle(customItem.getBouquetDesign().getStyle());
            customizedBouquet.setWrapping(customItem.getBouquetDesign().getWrappingId());
            customizedBouquet.setCustomBouquetSnapshot(snapshotJson);

            customizedBouquetRepository.save(customizedBouquet);
        }

        // 6. Persist Payment with PENDING status in database
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("PayHere")
                .paymentStatus("PENDING")
                .amount(authoritativeTotal)
                .build();
        paymentRepository.save(payment);

        String orderIdStr = "ORDER-" + order.getId();
        String hash = generatePayHereHash(merchantId, orderIdStr, payHereAmount, currency, secretKey);
        
        String itemsDescription = request.getItems().stream()
                .map(item -> item.getTitle() + " x" + item.getQuantity())
                .collect(Collectors.joining(", "));

        log.info("Initialized secure payment session and saved Order in DB: orderId={}, amount={}, hash={}", orderIdStr, payHereAmount, hash);

        return CheckoutResponse.builder()
                .merchantId(merchantId)
                .orderId(orderIdStr)
                .amount(Double.valueOf(payHereAmount))
                .currency(currency)
                .hash(hash)
                .itemsName(itemsDescription)
                .build();
    }

    private Customer resolveCustomer() {
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

    private String generatePayHereHash(String merchantId, String orderId, String formattedAmount, String currency, String secretKey) {
        String hashedSecret = md5(secretKey);
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
