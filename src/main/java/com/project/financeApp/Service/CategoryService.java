package com.project.financeApp.Service;

import com.project.financeApp.model.dto.CategoryRequestDTO;
import com.project.financeApp.model.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    List<CategoryResponseDTO> getAllCategories();
}
