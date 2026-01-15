package com.project.financeApp.model.dto;

import java.math.BigDecimal;

public record TrendDataDTO(
    String period,        // "2024-06-01" or "Week 1" or "January"
    BigDecimal income,
    BigDecimal expense,
    BigDecimal netCashFlow  // income - expense
) {}
