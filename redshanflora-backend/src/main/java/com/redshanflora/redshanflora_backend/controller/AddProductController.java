package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.service.AddProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class AddProductController {

    private final AddProductService addProductService;

    public AddProductController(AddProductService addProductService) {
        this.addProductService = addProductService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody AddProductDTO addProductDTO) {

        addProductService.addProduct(addProductDTO);

        return ResponseEntity.ok("Product added successfully.");
    }
}
