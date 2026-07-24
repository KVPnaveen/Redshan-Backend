package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.exception.EmailDeliveryException;
import com.redshanflora.redshanflora_backend.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProdEmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:${spring.mail.username:}}")
    private String fromAddress;

    @PostConstruct
    public void init() {
        log.info("Active EmailService: ProdEmailServiceImpl");
    }

    public ProdEmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        if (fromAddress != null && !fromAddress.trim().isEmpty()) {
            message.setFrom(fromAddress);
        }
        message.setTo(email);
        message.setSubject("Redshan Flora Password Reset Code");
        message.setText("Your Redshan Flora password reset code is: " + otp + "\n\n" +
                        "This code expires in 5 minutes.\n" +
                        "Do not share this code with anyone.");

        try {
            mailSender.send(message);
            log.info("Email OTP successfully sent to masked recipient");
        } catch (Exception e) {
            log.error("Email delivery failed", e);
            throw new EmailDeliveryException("We encountered an issue sending the verification email. Please try again later.", e);
        }
    }
}
