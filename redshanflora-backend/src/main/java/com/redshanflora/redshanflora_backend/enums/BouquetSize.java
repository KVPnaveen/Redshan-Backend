package com.redshanflora.redshanflora_backend.enums;

import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;
import java.math.BigDecimal;

public enum BouquetSize {
    SMALL("Small", 10, new BigDecimal("0.8")),
    MEDIUM("Medium", 20, new BigDecimal("1.0")),
    LARGE("Large", 35, new BigDecimal("1.25")),
    XLARGE("Extra Large", 50, new BigDecimal("1.5"));

    private final String label;
    private final int maxFlowers;
    private final BigDecimal multiplier;

    BouquetSize(String label, int maxFlowers, BigDecimal multiplier) {
        this.label = label;
        this.maxFlowers = maxFlowers;
        this.multiplier = multiplier;
    }

    public String getLabel() {
        return label;
    }

    public int getMaxFlowers() {
        return maxFlowers;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public static BouquetSize fromKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new CheckoutValidationException("Custom bouquet size key is missing.");
        }
        String cleanKey = key.trim().toUpperCase();
        try {
            return BouquetSize.valueOf(cleanKey);
        } catch (IllegalArgumentException e) {
            throw new CheckoutValidationException("Invalid bouquet size: " + key);
        }
    }
}
