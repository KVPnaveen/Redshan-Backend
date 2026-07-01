package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.entity.Category;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.SubCategory;
import com.redshanflora.redshanflora_backend.service.AddProductService;
import com.redshanflora.redshanflora_backend.repository.CategoryRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.SubCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class AddProductServiceImpl implements AddProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public AddProductServiceImpl(ProductRepository productRepository,
                                 CategoryRepository categoryRepository,
                                 SubCategoryRepository subCategoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public void addProduct(AddProductDTO dto) {

        Category category = categoryRepository.findById(dto.getCategoryId().longValue())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subCategory = subCategoryRepository.findById(dto.getSubCategoryId().longValue())
                .orElseThrow(() -> new RuntimeException("Sub Category not found"));

        Product product = new Product();

        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setImageUrl(dto.getImageUrl());

        productRepository.save(product);
    }
}
