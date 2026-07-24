package com.redshanflora.redshanflora_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redshanflora.redshanflora_backend.dto.product.BouquetDesignDto;
import com.redshanflora.redshanflora_backend.dto.product.BouquetFlowerInstanceDto;
import com.redshanflora.redshanflora_backend.dto.product.BouquetSnapshotDto;
import com.redshanflora.redshanflora_backend.dto.product.CoordinateDto;
import com.redshanflora.redshanflora_backend.dto.payment.BouquetPriceBreakdown;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.enums.BouquetSize;
import com.redshanflora.redshanflora_backend.enums.BouquetStyle;
import com.redshanflora.redshanflora_backend.exception.CheckoutValidationException;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.service.BouquetSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BouquetSnapshotServiceImpl implements BouquetSnapshotService {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public static final double MAX_POSITION_ABS = 10.0;
    public static final double MAX_ROTATION_ABS = 10.0;
    public static final double MIN_SCALE_ABS = 0.1;
    public static final double MAX_SCALE_ABS = 5.0;

    @Override
    public BouquetSnapshotDto buildSnapshot(BouquetDesignDto design, BouquetPriceBreakdown breakdown) {
        if (design == null) {
            throw new CheckoutValidationException("Custom bouquet design is missing.");
        }
        if (breakdown == null) {
            throw new CheckoutValidationException("Custom bouquet pricing breakdown is missing.");
        }

        String sizeKeyStr = design.getSizeKey();
        BouquetSize bouquetSize = BouquetSize.fromKey(sizeKeyStr);
        BouquetStyle bouquetStyle = BouquetStyle.fromKey(design.getBouquetStyle());

        List<BouquetFlowerInstanceDto> flowers = design.getFlowers();
        if (flowers == null || flowers.isEmpty()) {
            throw new CheckoutValidationException("Custom bouquet must contain at least one flower instance.");
        }

        if (flowers.size() > bouquetSize.getMaxFlowers()) {
            throw new CheckoutValidationException(bouquetSize.getLabel() + " size supports up to " +
                    bouquetSize.getMaxFlowers() + " flowers. Selected: " + flowers.size());
        }

        // Sanitize individual flowers & duplicate check
        Set<String> instanceIds = new HashSet<>();
        List<BouquetSnapshotDto.FlowerInstanceSnapshot> instanceSnapshots = new ArrayList<>();
        Map<Long, Integer> quantityMap = new HashMap<>();

        for (BouquetFlowerInstanceDto flower : flowers) {
            if (flower == null) {
                throw new CheckoutValidationException("Flower instance entry cannot be null.");
            }
            if (flower.getInstanceId() == null || flower.getInstanceId().trim().isEmpty()) {
                throw new CheckoutValidationException("Flower instance ID is missing.");
            }
            if (!instanceIds.add(flower.getInstanceId())) {
                throw new CheckoutValidationException("Duplicate flower instance ID detected: " + flower.getInstanceId());
            }
            if (flower.getProductId() == null) {
                throw new CheckoutValidationException("Flower product ID is missing.");
            }

            // Sanitize position
            CoordinateDto pos = flower.getPosition();
            if (pos == null || pos.getX() == null || pos.getY() == null || pos.getZ() == null) {
                throw new CheckoutValidationException("Flower instance position coordinates are missing.");
            }
            if (!Double.isFinite(pos.getX()) || !Double.isFinite(pos.getY()) || !Double.isFinite(pos.getZ())) {
                throw new CheckoutValidationException("Flower instance position coordinates must be finite numbers.");
            }
            if (Math.abs(pos.getX()) > MAX_POSITION_ABS || Math.abs(pos.getY()) > MAX_POSITION_ABS || Math.abs(pos.getZ()) > MAX_POSITION_ABS) {
                throw new CheckoutValidationException("Flower instance position coordinates out of bounds: " + pos);
            }

            // Sanitize rotation
            CoordinateDto rot = flower.getRotation();
            if (rot == null || rot.getX() == null || rot.getY() == null || rot.getZ() == null) {
                throw new CheckoutValidationException("Flower instance rotation coordinates are missing.");
            }
            if (!Double.isFinite(rot.getX()) || !Double.isFinite(rot.getY()) || !Double.isFinite(rot.getZ())) {
                throw new CheckoutValidationException("Flower instance rotation coordinates must be finite numbers.");
            }
            if (Math.abs(rot.getX()) > MAX_ROTATION_ABS || Math.abs(rot.getY()) > MAX_ROTATION_ABS || Math.abs(rot.getZ()) > MAX_ROTATION_ABS) {
                throw new CheckoutValidationException("Flower instance rotation coordinates out of bounds: " + rot);
            }

            // Sanitize scale
            CoordinateDto scl = flower.getScale();
            if (scl == null || scl.getX() == null || scl.getY() == null || scl.getZ() == null) {
                throw new CheckoutValidationException("Flower instance scale coordinates are missing.");
            }
            if (!Double.isFinite(scl.getX()) || !Double.isFinite(scl.getY()) || !Double.isFinite(scl.getZ())) {
                throw new CheckoutValidationException("Flower instance scale coordinates must be finite numbers.");
            }
            if (scl.getX() < MIN_SCALE_ABS || scl.getX() > MAX_SCALE_ABS ||
                scl.getY() < MIN_SCALE_ABS || scl.getY() > MAX_SCALE_ABS ||
                scl.getZ() < MIN_SCALE_ABS || scl.getZ() > MAX_SCALE_ABS) {
                throw new CheckoutValidationException("Flower instance scale coordinates out of bounds: " + scl);
            }

            // Load and verify product from database
            Product product = productRepository.findById(flower.getProductId())
                    .orElseThrow(() -> new CheckoutValidationException("Product not found with ID: " + flower.getProductId()));

            if (product.getCategory() == null || !"Individual Flowers".equalsIgnoreCase(product.getCategory().getCategoryName())) {
                throw new CheckoutValidationException("Product is not an Individual Flower: " + flower.getProductId());
            }

            BigDecimal unitPrice = product.getPrice();
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                throw new CheckoutValidationException("Product price is missing or invalid in database for ID: " + flower.getProductId());
            }

            quantityMap.put(flower.getProductId(), quantityMap.getOrDefault(flower.getProductId(), 0) + 1);

            instanceSnapshots.add(BouquetSnapshotDto.FlowerInstanceSnapshot.builder()
                    .instanceId(flower.getInstanceId())
                    .productId(product.getId())
                    .productName(product.getProductName())
                    .imageUrl(product.getImageUrl())
                    .modelUrl(product.getModelUrl())
                    .unitPrice(unitPrice)
                    .position(pos)
                    .rotation(rot)
                    .scale(scl)
                    .build());
        }

        // Build flowerSummary
        List<BouquetSnapshotDto.FlowerSummarySnapshot> summarySnapshots = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : quantityMap.entrySet()) {
            Long pid = entry.getKey();
            Integer qty = entry.getValue();

            // Find matching instance snapshot to get unit price and name
            BouquetSnapshotDto.FlowerInstanceSnapshot matched = instanceSnapshots.stream()
                    .filter(i -> Objects.equals(i.getProductId(), pid))
                    .findFirst()
                    .orElseThrow(() -> new CheckoutValidationException("Instance match failed during summary builder."));

            summarySnapshots.add(BouquetSnapshotDto.FlowerSummarySnapshot.builder()
                    .productId(pid)
                    .productName(matched.getProductName())
                    .quantity(qty)
                    .unitPrice(matched.getUnitPrice())
                    .lineTotal(matched.getUnitPrice().multiply(BigDecimal.valueOf(qty)))
                    .build());
        }

        return BouquetSnapshotDto.builder()
                .snapshotVersion(1)
                .type("CUSTOM_BOUQUET")
                .size(BouquetSnapshotDto.SizeSnapshot.builder()
                        .key(bouquetSize.name())
                        .label(bouquetSize.getLabel())
                        .maxFlowers(bouquetSize.getMaxFlowers())
                        .multiplier(bouquetSize.getMultiplier())
                        .build())
                .style(design.getStyle())
                .bouquetStyle(bouquetStyle.name())
                .wrappingId(design.getWrappingId())
                .ribbonId(design.getRibbonId())
                .flowers(instanceSnapshots)
                .flowerSummary(summarySnapshots)
                .pricing(BouquetSnapshotDto.PricingSnapshot.builder()
                        .flowerSubtotal(breakdown.getFlowerSubtotal())
                        .sizeAdjustment(breakdown.getSizeAdjustment())
                        .scaledFlowerTotal(breakdown.getScaledFlowerTotal())
                        .serviceCharge(breakdown.getServiceCharge())
                        .grandTotal(breakdown.getGrandTotal())
                        .build())
                .build();
    }

    @Override
    public String serializeSnapshot(BouquetSnapshotDto snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            log.error("Failed to serialize bouquet snapshot DTO", e);
            throw new CheckoutValidationException("Custom bouquet snapshot serialization failed: " + e.getMessage());
        }
    }

    @Override
    public BouquetSnapshotDto deserializeSnapshot(String snapshotJson) {
        if (snapshotJson == null || snapshotJson.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(snapshotJson, BouquetSnapshotDto.class);
        } catch (Exception e) {
            log.error("Failed to deserialize old custom bouquet snapshot JSON", e);
            // Catch error, log it, return null snapshot so order pages load safely instead of crashing
            return null;
        }
    }
}
