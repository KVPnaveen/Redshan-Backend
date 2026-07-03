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
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<CheckoutResponse> createPayment(@RequestBody CheckoutRequest request) {
        CheckoutResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> receiveNotification(@RequestParam("merchant_id") String merchantId,
                                                    @RequestParam("order_id") String orderId,
                                                    @RequestParam("payment_id") String paymentId,
                                                    @RequestParam("payhere_amount") String payhereAmount,
                                                    @RequestParam("payhere_currency") String payhereCurrency,
                                                    @RequestParam("status_code") int statusCode,
                                                    @RequestParam("md5sig") String md5sig) {
        paymentService.handleNotification(merchantId, orderId, paymentId, payhereAmount, payhereCurrency, statusCode, md5sig);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String orderId) {
        return paymentService.getPaymentStatus(orderId);
    }
}
