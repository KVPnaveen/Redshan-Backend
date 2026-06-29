package com.redshanflora.redshanflora_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWishlistItemException extends RuntimeException {
    public DuplicateWishlistItemException(String message) {
        super(message);
    }
}
