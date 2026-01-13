package com.project.financeApp.Service.impl;

import com.project.financeApp.Service.CategoryService;
import com.project.financeApp.Service.UserService;
import com.project.financeApp.mapper.CategoryMapper;
import com.project.financeApp.model.dto.CategoryRequestDTO;
import com.project.financeApp.model.dto.CategoryResponseDTO;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;
import com.project.financeApp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        User user = userService.getCurrentUser();

        Category category = CategoryMapper.toEntity(request, user);
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        User user = userService.getCurrentUser();

        return categoryRepository.findByUser(user)
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }
}

