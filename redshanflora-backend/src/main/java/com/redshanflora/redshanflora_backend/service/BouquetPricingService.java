package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.product.BouquetDesignDto;
import com.redshanflora.redshanflora_backend.dto.payment.BouquetPriceBreakdown;

public interface BouquetPricingService {
    BouquetPriceBreakdown validateAndCalculatePrice(BouquetDesignDto bouquetDesign);
}
