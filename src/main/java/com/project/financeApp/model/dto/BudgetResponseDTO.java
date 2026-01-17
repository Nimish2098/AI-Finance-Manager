package com.project.financeApp.model.dto;

import java.math.BigDecimal;
import java.util.UUID;


public record BudgetResponseDTO(
        UUID id,
        UUID categoryId,      // needed for editing
        String categoryName,
        BigDecimal budgetAmount,
        BigDecimal spentAmount,
        int month,
        int year
) {}

