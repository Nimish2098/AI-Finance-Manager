package com.project.financeApp.model.dto;

import java.math.BigDecimal;

public record MonthlySummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netSavings
) {}