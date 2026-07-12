package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.dto.product.AddProductResponseDTO;
import com.redshanflora.redshanflora_backend.service.AddProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class AddProductController {

    private final AddProductService addProductService;

    public AddProductController(AddProductService addProductService) {
        this.addProductService = addProductService;
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AddProductResponseDTO> addProduct(

            @RequestPart("product") AddProductDTO addProductDTO,
            @RequestPart("image") MultipartFile image) {

        log.info("Received request to add product: {}", addProductDTO.getProductName());

        AddProductResponseDTO response =
                addProductService.addProduct(addProductDTO, image);

        log.info("Product added successfully. Product ID: {}, Name: {}",
                response.getId(),
                response.getProductName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}