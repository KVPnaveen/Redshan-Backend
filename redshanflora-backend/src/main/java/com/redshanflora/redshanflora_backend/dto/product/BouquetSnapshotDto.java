package com.redshanflora.redshanflora_backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BouquetSnapshotDto {
    @Builder.Default
    private Integer snapshotVersion = 1;
    
    @Builder.Default
    private String type = "CUSTOM_BOUQUET";
    
    private SizeSnapshot size;
    private String style;
    private String bouquetStyle;
    private String wrappingId;
    private String ribbonId;
    private List<FlowerInstanceSnapshot> flowers;
    private List<FlowerSummarySnapshot> flowerSummary;
    private PricingSnapshot pricing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SizeSnapshot {
        private String key;
        private String label;
        private Integer maxFlowers;
        private BigDecimal multiplier;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowerInstanceSnapshot {
        private String instanceId;
        private Long productId;
        private String productName;
        private String imageUrl;
        private String modelUrl;
        private BigDecimal unitPrice;
        private CoordinateDto position;
        private CoordinateDto rotation;
        private CoordinateDto scale;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowerSummarySnapshot {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingSnapshot {
        private BigDecimal flowerSubtotal;
        private BigDecimal sizeAdjustment;
        private BigDecimal scaledFlowerTotal;
        private BigDecimal serviceCharge;
        private BigDecimal grandTotal;
    }
}
