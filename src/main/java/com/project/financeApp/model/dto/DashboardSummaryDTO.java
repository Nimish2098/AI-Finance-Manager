package com.project.financeApp.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netSavings,
        List<CategoryBreakdownDTO> expenseByCategory
) {}

