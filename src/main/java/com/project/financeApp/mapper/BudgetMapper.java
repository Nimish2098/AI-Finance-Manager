package com.project.financeApp.mapper;

import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;

import java.math.BigDecimal;

public class BudgetMapper {

    private BudgetMapper() {}

    public static Budget toEntity(
            BudgetRequestDTO dto,
            User user,
            Category category
    ) {
        return Budget.builder()
                .budgetAmount(dto.budgetAmount())
                .month(dto.month())
                .year(dto.year())
                .user(user)
                .category(category)
                .build();
    }

    public static BudgetResponseDTO toResponse(Budget entity, BigDecimal spentAmount) {
        return new BudgetResponseDTO(
                entity.getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getBudgetAmount(),
                spentAmount,
                entity.getMonth(),
                entity.getYear()
        );
    }
}
