package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.AccountType;

import java.math.BigDecimal;

public record AccountRequestDTO(
        String name,
        AccountType type,
        BigDecimal balance
) {}