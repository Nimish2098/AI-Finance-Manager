package com.project.financeApp.mapper;


import com.project.financeApp.model.dto.CategoryRequestDTO;
import com.project.financeApp.model.dto.CategoryResponseDTO;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;


public class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(CategoryRequestDTO dto, User user) {
        return Category.builder()
                .name(dto.name())
                .type(dto.type()) // ✅ TransactionType
                .user(user)
                .build();
    }

    public static CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getType()
        );
    }

}