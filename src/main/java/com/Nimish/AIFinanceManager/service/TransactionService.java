package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Account;
import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.repository.AccountRepository;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction saveTransaction(Transaction transaction,Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new RuntimeException("Account not Found"));
        transaction.setAccount(account);

        if(transaction.getType().equalsIgnoreCase("Income")){
            account.setBalance(account.getBalance()+transaction.getAmount());
        }
        else if(transaction.getType().equalsIgnoreCase("Expense")){
            account.setBalance(account.getBalance()-transaction.getAmount());
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }


    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }

    public String updateTransaction(Long transactionId,Transaction transaction){
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setDate(transaction.getDate());
        savedTransaction.setType(transaction.getType());
        savedTransaction.setCategory(transaction.getCategory());
        transactionRepository.save(savedTransaction);
        return "Transaction updated Successfully";
    }

    public String deleteTransaction(Long transactionId){
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        transactionRepository.delete(savedTransaction);
        return "Transaction deleted Successfully";
    }
}
