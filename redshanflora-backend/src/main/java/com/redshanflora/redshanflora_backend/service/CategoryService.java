package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}
