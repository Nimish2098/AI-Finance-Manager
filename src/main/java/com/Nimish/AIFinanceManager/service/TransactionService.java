package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public String saveTransaction(Transaction transaction){
        repository.save(transaction);
        return "Transaction Saved Successfully";
    }


    public List<Transaction> getAllTransaction(){
        return repository.findAll();
    }

    public String updateTransaction(Long transactionId,Transaction transaction){
        Transaction savedTransaction = repository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setDate(transaction.getDate());
        savedTransaction.setType(transaction.getType());
        savedTransaction.setCategory(transaction.getCategory());
        savedTransaction.setDescription(transaction.getDescription());
        repository.save(savedTransaction);
        return "Transaction updated Successfully";
    }

    public String deleteTransaction(Long transactionId){
        Transaction savedTransaction = repository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        repository.delete(savedTransaction);
        return "Transaction deleted Successfully";
    }
}
