package com.redshanflora.redshanflora_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoyaltyException extends RuntimeException {
    public LoyaltyException(String message) {
        super(message);
    }
}
