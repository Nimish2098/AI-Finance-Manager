package com.project.financeApp.controller;


import com.project.financeApp.Service.AccountService;
import com.project.financeApp.model.dto.AccountRequestDTO;
import com.project.financeApp.model.dto.AccountResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public AccountResponseDTO createAccount(
            @RequestBody AccountRequestDTO request
    ) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public List<AccountResponseDTO> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponseDTO getAccountById(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }
}

