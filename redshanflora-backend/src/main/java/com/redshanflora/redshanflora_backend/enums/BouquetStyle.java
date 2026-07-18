package com.redshanflora.redshanflora_backend.enums;

import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;

public enum BouquetStyle {
    ROUND,
    HEART,
    SPIRAL;

    public static BouquetStyle fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return ROUND;
        }
        String cleanKey = key.trim().toUpperCase();
        try {
            return BouquetStyle.valueOf(cleanKey);
        } catch (IllegalArgumentException e) {
            throw new CheckoutValidationException("Invalid bouquet style: " + key);
        }
    }
}
