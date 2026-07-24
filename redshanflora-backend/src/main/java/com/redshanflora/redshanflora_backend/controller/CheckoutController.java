package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import com.redshanflora.redshanflora_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class CheckoutController {

    private final PaymentService paymentService;

    @PostMapping("/api/checkout")
    public ResponseEntity<CheckoutResponse> initializeCheckout(@RequestBody CheckoutRequest request) {
        log.info("Delegating checkout initialization request to PaymentService");
        CheckoutResponse response = paymentService.createPayment(request);
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
        
        log.info("Delegating PayHere notification to PaymentService: orderId={}", orderId);
        paymentService.handleNotification(merchantId, orderId, paymentId, payhereAmount, payhereCurrency, statusCode, md5sig);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/checkout/status/{orderId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String orderId) {
        log.info("Delegating retrieve payment status to PaymentService: orderId={}", orderId);
        return paymentService.getPaymentStatus(orderId);
    }
}
