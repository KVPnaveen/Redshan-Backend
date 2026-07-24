package com.redshanflora.redshanflora_backend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BouquetPriceBreakdown {
    private BigDecimal flowerSubtotal;
    private String sizeKey;
    private String sizeLabel;
    private BigDecimal sizeMultiplier;
    private BigDecimal sizeAdjustment;
    private BigDecimal scaledFlowerTotal;
    private BigDecimal serviceCharge;
    private BigDecimal grandTotal;
}
