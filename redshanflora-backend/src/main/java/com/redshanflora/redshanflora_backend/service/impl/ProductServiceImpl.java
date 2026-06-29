package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.category.CategoryResponse;
import com.redshanflora.redshanflora_backend.dto.product.ProductRequest;
import com.redshanflora.redshanflora_backend.dto.product.ProductResponse;
import com.redshanflora.redshanflora_backend.dto.category.SubCategoryResponse;
import com.redshanflora.redshanflora_backend.entity.Category;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.SubCategory;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CategoryRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.SubCategoryRepository;
import com.redshanflora.redshanflora_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategory_Id(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsBySubCategoryId(Long subCategoryId) {
        if (!subCategoryRepository.existsById(subCategoryId)) {
            throw new ResourceNotFoundException("Subcategory not found with id: " + subCategoryId);
        }
        return productRepository.findBySubCategory_Id(subCategoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        // Since the 'featured' column is removed in the new schema, we fallback to returning the first 8 products.
        return productRepository.findAll().stream()
                .limit(8)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubCategoryId()));
        }

        Product product = Product.builder()
                .category(category)
                .subCategory(subCategory)
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .imageUrl(request.getImageUrl())
                .discountPercentage(request.getDiscountPercentage() != null ? request.getDiscountPercentage() : java.math.BigDecimal.ZERO)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + request.getSubCategoryId()));
        }

        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        product.setImageUrl(request.getImageUrl());
        if (request.getDiscountPercentage() != null) {
            product.setDiscountPercentage(request.getDiscountPercentage());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getCategoryName())
                .subCategoryId(product.getSubCategory() != null ? product.getSubCategory().getId() : null)
                .subCategoryName(product.getSubCategory() != null ? product.getSubCategory().getSubCategoryName() : null)
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .discountPercentage(product.getDiscountPercentage())
                .category(CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .categoryName(product.getCategory().getCategoryName())
                        .description(product.getCategory().getDescription())
                        .build())
                .subCategory(product.getSubCategory() != null ? SubCategoryResponse.builder()
                        .id(product.getSubCategory().getId())
                        .categoryId(product.getCategory().getId())
                        .subCategoryName(product.getSubCategory().getSubCategoryName())
                        .build() : null)
                .build();
    }
}
