package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.product.BouquetDesignDto;
import com.redshanflora.redshanflora_backend.dto.product.BouquetSnapshotDto;
import com.redshanflora.redshanflora_backend.dto.payment.BouquetPriceBreakdown;

public interface BouquetSnapshotService {
    BouquetSnapshotDto buildSnapshot(BouquetDesignDto design, BouquetPriceBreakdown breakdown);
    String serializeSnapshot(BouquetSnapshotDto snapshot);
    BouquetSnapshotDto deserializeSnapshot(String snapshotJson);
}
