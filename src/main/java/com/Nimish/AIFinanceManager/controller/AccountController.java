package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.model.Account;
import com.Nimish.AIFinanceManager.repository.AccountRepository;
import com.Nimish.AIFinanceManager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    @GetMapping
    public List<Account> getAllAccount(){
       return  accountService.getAllAccounts();
    }

    @PostMapping("/add")
    public Account addAccount(@RequestBody Account account){
        return accountService.saveAccount(account);
    }

    @PostMapping("/update/{id}")
    public Account updateAccount(@PathVariable Long id,@RequestBody Account account){
        return accountService.updateAccount(id,account);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id){
        accountService.deleteAccount(id);
         return "Account deleted successfully";
    }
}
