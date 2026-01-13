package com.project.financeApp.mapper;


import com.project.financeApp.model.dto.TransactionRequestDTO;
import com.project.financeApp.model.dto.TransactionResponseDTO;
import com.project.financeApp.model.entity.Account;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.Transaction;
import com.project.financeApp.model.entity.User;

public class TransactionMapper {

    private TransactionMapper() {}

    public static Transaction toEntity(
            TransactionRequestDTO dto,
            User user,
            Account account,
            Category category
    ) {
        return Transaction.builder()
                .amount(dto.amount())
                .type(dto.type())
                .description(dto.description())
                .transactionDate(dto.transactionDate())
                .user(user)
                .account(account)
                .category(category)
                .build();
    }

    public static TransactionResponseDTO toResponse(Transaction entity) {

        return new TransactionResponseDTO(
                entity.getId(),
                entity.getAmount(),
                entity.getType(),
                entity.getAccount() != null
                        ? entity.getAccount().getName()
                        : null,
                entity.getCategory() != null
                        ? entity.getCategory().getName()
                        : null,
                entity.getDescription(),
                entity.getTransactionDate()
        );
    }

}

