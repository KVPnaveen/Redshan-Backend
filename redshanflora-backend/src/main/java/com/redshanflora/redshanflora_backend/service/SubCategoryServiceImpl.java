package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.SubCategoryResponse;
import com.redshanflora.redshanflora_backend.entity.SubCategory;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CategoryRepository;
import com.redshanflora.redshanflora_backend.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<SubCategoryResponse> getAllSubCategories() {
        return subCategoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubCategoryResponse getSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + id));
        return mapToResponse(subCategory);
    }

    @Override
    public List<SubCategoryResponse> getSubCategoriesByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return subCategoryRepository.findByCategory_Id(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubCategoryResponse mapToResponse(SubCategory subCategory) {
        return SubCategoryResponse.builder()
                .id(subCategory.getId())
                .categoryId(subCategory.getCategory().getId())
                .subCategoryName(subCategory.getSubCategoryName())
                .description(null)
                .createdAt(null)
                .build();
    }
}
