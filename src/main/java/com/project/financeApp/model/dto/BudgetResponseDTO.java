package com.project.financeApp.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponseDTO(
        UUID id,
        String categoryName,
        BigDecimal limitAmount,
        int month,
        int year
) {}

