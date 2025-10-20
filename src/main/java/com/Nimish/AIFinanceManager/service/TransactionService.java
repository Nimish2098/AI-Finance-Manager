package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;

import java.util.List;

public class TransactionService {

    private TransactionRepository repository;

    public String saveTransaction(Transaction transaction){
        repository.save(transaction);
        return "Transaction Saved Successfully";
    }


    public List<Transaction> getAllTransaction(){
        return repository.findAll();
    }

    public String updateTransaction(Long transactionId,Transaction transaction){
        Transaction savedTransaction = repository.
    }
}
