package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.ProductRequest;
import com.redshanflora.redshanflora_backend.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    List<ProductResponse> getProductsByCategoryId(Long categoryId);
    List<ProductResponse> getProductsBySubCategoryId(Long subCategoryId);
    List<ProductResponse> getFeaturedProducts();
    List<ProductResponse> searchProducts(String keyword);
    
    // CRUD operations (Manager Ready)
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
