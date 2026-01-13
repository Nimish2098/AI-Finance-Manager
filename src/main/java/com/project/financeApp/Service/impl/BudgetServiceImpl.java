package com.project.financeApp.Service.impl;


import com.project.financeApp.Service.BudgetService;
import com.project.financeApp.Service.UserService;
import com.project.financeApp.mapper.BudgetMapper;
import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;
import com.project.financeApp.repository.BudgetRepository;
import com.project.financeApp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public BudgetResponseDTO createBudget(BudgetRequestDTO request) {
        User user = userService.getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Budget budget = BudgetMapper.toEntity(request, user, category);
        return BudgetMapper.toResponse(budgetRepository.save(budget));
    }
}

