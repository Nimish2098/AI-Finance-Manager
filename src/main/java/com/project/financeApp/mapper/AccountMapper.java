package com.project.financeApp.mapper;


import com.project.financeApp.model.dto.AccountRequestDTO;
import com.project.financeApp.model.dto.AccountResponseDTO;
import com.project.financeApp.model.entity.Account;
import com.project.financeApp.model.entity.User;

public class AccountMapper {

    private AccountMapper() {}

    public static Account toEntity(AccountRequestDTO dto, User user) {
        return Account.builder()
                .name(dto.name())
                .type(dto.type())
                .balance(dto.balance())
                .user(user)
                .build();
    }

    public static AccountResponseDTO toResponse(Account entity) {
        return new AccountResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getBalance()
        );
    }
}

