package com.project.financeApp.model.dto;

import com.project.financeApp.model.entity.TransactionType;

import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        TransactionType type
) {}

