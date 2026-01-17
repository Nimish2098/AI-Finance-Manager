package com.project.financeApp.model.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record BudgetStatusDTO(
        UUID budgetId,
        String categoryName,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double percentageUsed,
        boolean isExceeded,
        int month,
        int year
) {}
