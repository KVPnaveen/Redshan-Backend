package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.SubCategoryResponse;
import java.util.List;

public interface SubCategoryService {
    List<SubCategoryResponse> getAllSubCategories();
    SubCategoryResponse getSubCategoryById(Long id);
    List<SubCategoryResponse> getSubCategoriesByCategoryId(Long categoryId);
}
