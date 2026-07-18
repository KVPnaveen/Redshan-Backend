package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.product.BouquetDesignDto;
import com.redshanflora.redshanflora_backend.dto.product.BouquetFlowerInstanceDto;
import com.redshanflora.redshanflora_backend.dto.payment.BouquetPriceBreakdown;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.enums.BouquetSize;
import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.service.BouquetPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class BouquetPricingServiceImpl implements BouquetPricingService {

    private final ProductRepository productRepository;

    private static final BigDecimal SERVICE_CHARGE = new BigDecimal("500.00");
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    @Override
    public BouquetPriceBreakdown validateAndCalculatePrice(BouquetDesignDto bouquetDesign) {
        if (bouquetDesign == null) {
            throw new CheckoutValidationException("Custom bouquet design is missing.");
        }

        String sizeKeyStr = bouquetDesign.getSizeKey();
        BouquetSize bouquetSize = BouquetSize.fromKey(sizeKeyStr);

        if (bouquetDesign.getFlowers() == null) {
            throw new CheckoutValidationException("Flower list is missing.");
        }
        if (bouquetDesign.getFlowers().isEmpty()) {
            throw new CheckoutValidationException("Custom bouquet must contain at least one flower.");
        }

        // Group instances by productId to derive quantities
        Map<Long, Integer> quantityMap = new HashMap<>();
        for (BouquetFlowerInstanceDto flower : bouquetDesign.getFlowers()) {
            if (flower == null) {
                throw new CheckoutValidationException("Flower instance entry cannot be null.");
            }
            if (flower.getProductId() == null) {
                throw new CheckoutValidationException("Flower product ID is missing.");
            }
            quantityMap.put(flower.getProductId(), quantityMap.getOrDefault(flower.getProductId(), 0) + 1);
        }

        long totalFlowersCount = bouquetDesign.getFlowers().size();
        BigDecimal flowerSubtotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : quantityMap.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new CheckoutValidationException("Product not found with ID: " + productId));

            if (product.getCategory() == null || !"Individual Flowers".equalsIgnoreCase(product.getCategory().getCategoryName())) {
                throw new CheckoutValidationException("Product is not an Individual Flower: " + productId);
            }

            BigDecimal productPrice = product.getPrice();
            if (productPrice == null) {
                throw new CheckoutValidationException("Product price is missing in the database: " + productId);
            }
            if (productPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new CheckoutValidationException("Product price is invalid/negative in the database: " + productId);
            }

            BigDecimal lineSubtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
            flowerSubtotal = flowerSubtotal.add(lineSubtotal);
        }

        // Validate limits
        if (totalFlowersCount > bouquetSize.getMaxFlowers()) {
            throw new CheckoutValidationException(bouquetSize.getLabel() + " size supports up to " +
                    bouquetSize.getMaxFlowers() + " flowers. Selected: " + totalFlowersCount);
        }

        BigDecimal sizeMultiplier = bouquetSize.getMultiplier();
        BigDecimal scaledFlowerTotal = flowerSubtotal.multiply(sizeMultiplier).setScale(MONEY_SCALE, MONEY_ROUNDING);
        BigDecimal sizeAdjustment = scaledFlowerTotal.subtract(flowerSubtotal).setScale(MONEY_SCALE, MONEY_ROUNDING);
        BigDecimal serviceCharge = totalFlowersCount > 0 ? SERVICE_CHARGE : BigDecimal.ZERO;
        BigDecimal grandTotal = scaledFlowerTotal.add(serviceCharge).setScale(MONEY_SCALE, MONEY_ROUNDING);

        return BouquetPriceBreakdown.builder()
                .flowerSubtotal(flowerSubtotal.setScale(MONEY_SCALE, MONEY_ROUNDING))
                .sizeKey(bouquetSize.name())
                .sizeLabel(bouquetSize.getLabel())
                .sizeMultiplier(sizeMultiplier)
                .sizeAdjustment(sizeAdjustment)
                .scaledFlowerTotal(scaledFlowerTotal)
                .serviceCharge(serviceCharge)
                .grandTotal(grandTotal)
                .build();
    }
}

