package com.project.financeApp.Service;

import com.project.financeApp.model.dto.CategoryRequestDTO;
import com.project.financeApp.model.dto.CategoryResponseDTO;
import org.hibernate.type.descriptor.java.UUIDJavaType;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO updateCategory(UUID categoryID, CategoryRequestDTO request);

    void deleteCategory(UUID categoryId);
}
