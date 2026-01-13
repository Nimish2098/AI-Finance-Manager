package com.project.financeApp.Service;


import com.project.financeApp.model.dto.AccountRequestDTO;
import com.project.financeApp.model.dto.AccountResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponseDTO createAccount(AccountRequestDTO request);

    List<AccountResponseDTO> getAllAccounts();

    AccountResponseDTO getAccountById(UUID accountId);
}
