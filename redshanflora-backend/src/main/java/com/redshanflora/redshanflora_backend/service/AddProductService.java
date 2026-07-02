package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.dto.product.AddProductResponseDTO;

public interface AddProductService {
    AddProductResponseDTO addProduct(AddProductDTO dto);
}

