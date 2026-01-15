package com.project.financeApp.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record CashFlowDTO(
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    BigDecimal totalInflow,
    BigDecimal totalOutflow,
    List<DailyCashFlowDTO> dailyFlow  
) {}
