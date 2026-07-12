package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.product.AddProductDTO;
import com.redshanflora.redshanflora_backend.dto.product.AddProductResponseDTO;
import com.redshanflora.redshanflora_backend.entity.Category;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.SubCategory;
import com.redshanflora.redshanflora_backend.repository.CategoryRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.SubCategoryRepository;
import com.redshanflora.redshanflora_backend.service.AddProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
    public AddProductResponseDTO addProduct(AddProductDTO dto, MultipartFile image) {

        Category category = categoryRepository.findById(dto.getCategoryId().longValue())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subCategory = subCategoryRepository.findById(dto.getSubCategoryId().longValue())
                .orElseThrow(() -> new RuntimeException("Sub Category not found"));

        // Save Image
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {

            try {

                String uploadDir = "uploads/products/";

                Files.createDirectories(Paths.get(uploadDir));

                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();

                Path filePath = Paths.get(uploadDir, fileName);

                image.transferTo(filePath);

                imageUrl = "/uploads/products/" + fileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        Product product = new Product();

        product.setCategory(category);
        product.setSubCategory(subCategory);
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());

        // Store generated image URL
        product.setImageUrl(imageUrl);

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    private AddProductResponseDTO mapToResponse(Product product) {

        AddProductResponseDTO response = new AddProductResponseDTO();

        response.setId(product.getId());
        response.setCategoryId(product.getCategory().getId().intValue());
        response.setSubCategoryId(product.getSubCategory().getId().intValue());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());

        return response;
    }
}