package com.project.financeApp.controller;


import com.project.financeApp.Service.CategoryService;
import com.project.financeApp.model.dto.CategoryRequestDTO;
import com.project.financeApp.model.dto.CategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDTO createCategory(
            @RequestBody CategoryRequestDTO request
    ) {
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {

        return categoryService.getAllCategories();
    }

    @DeleteMapping
    public void deleteCategory(@PathVariable UUID id){
        categoryService.deleteCategory(id);
    }
}

