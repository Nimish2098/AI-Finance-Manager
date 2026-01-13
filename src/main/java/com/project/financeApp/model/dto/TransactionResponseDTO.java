package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        String accountName,
        String categoryName,
        String description,
        LocalDate transactionDate
) {}

