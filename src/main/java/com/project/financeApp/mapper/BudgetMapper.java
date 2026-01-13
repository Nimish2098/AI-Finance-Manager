package com.project.financeApp.mapper;

import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;

public class BudgetMapper {

    private BudgetMapper() {}

    public static Budget toEntity(
            BudgetRequestDTO dto,
            User user,
            Category category
    ) {
        return Budget.builder()
                .limitAmount(dto.limitAmount())
                .month(dto.month())
                .year(dto.year())
                .user(user)
                .category(category)
                .build();
    }

    public static BudgetResponseDTO toResponse(Budget entity) {
        return new BudgetResponseDTO(
                entity.getId(),
                entity.getCategory().getName(),
                entity.getLimitAmount(),
                entity.getMonth(),
                entity.getYear()
        );
    }
}

