package com.project.financeApp.model.dto;

import java.math.BigDecimal;

public record CategoryBreakdownDTO(
        String category,
        BigDecimal amount
) {}

