package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequestDTO(
        BigDecimal amount,
        TransactionType type,
        UUID accountId,
        UUID categoryId,
        String description,
        LocalDate transactionDate
) {}

