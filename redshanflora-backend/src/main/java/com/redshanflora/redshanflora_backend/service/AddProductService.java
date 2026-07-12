package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.dto.product.AddProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;


public interface AddProductService {
    AddProductResponseDTO addProduct(AddProductDTO dto,
                                     MultipartFile image);;
}

