package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.payment.CheckoutRequest;
import com.redshanflora.redshanflora_backend.dto.payment.CheckoutResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PaymentService {
    CheckoutResponse createPayment(CheckoutRequest request);
    void handleNotification(String merchantId, String orderId, String paymentId, String payhereAmount,
                            String payhereCurrency, int statusCode, String md5sig);
    ResponseEntity<Map<String, String>> getPaymentStatus(String orderId);
}
