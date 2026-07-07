package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.product.ProductRequest;
import com.redshanflora.redshanflora_backend.dto.product.ProductResponse;
import com.redshanflora.redshanflora_backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySubCategoryId(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(productService.getProductsBySubCategoryId(subCategoryId));
    }

    @GetMapping("/category/{categoryId}/subcategory/{subCategoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryIdAndSubCategoryId(
            @PathVariable Long categoryId,
            @PathVariable Long subCategoryId) {
        return ResponseEntity.ok(productService.getProductsByCategoryIdAndSubCategoryId(categoryId, subCategoryId));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
