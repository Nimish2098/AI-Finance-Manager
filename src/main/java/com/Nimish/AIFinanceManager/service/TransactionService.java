package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.model.User;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction saveTransaction(Transaction transaction, Long userId) {
        User user = new User();
        user.setUserId(userId);
        transactiono.setUser(user);
        return transactionRepository.save(transaction);
    }

    /**
     * Finds all transactions for a specific user.
     * Assumes TransactionRepository has a method: List<Transaction> findByUserId(Long userId);
     */
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    public String updateTransaction(Long transactionId, Transaction transaction) {
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setDate(transaction.getDate());
        savedTransaction.setType(transaction.getType());
        savedTransaction.setCategory(transaction.getCategory());
        transactionRepository.save(savedTransaction);
        return "Transaction updated Successfully";
    }

    public String deleteTransaction(Long transactionId) {
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found."));
        transactionRepository.delete(savedTransaction);
        return "Transaction deleted Successfully";
    }
}