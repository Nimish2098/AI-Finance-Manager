package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.TransactionType;


public record CategoryRequestDTO(
        String name,
        TransactionType type   // ✅ NOT AccountType
) {}