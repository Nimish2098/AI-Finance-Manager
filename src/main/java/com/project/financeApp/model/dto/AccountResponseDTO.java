package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponseDTO(
        UUID id,
        String name,
        AccountType type,
        BigDecimal balance
) {}
