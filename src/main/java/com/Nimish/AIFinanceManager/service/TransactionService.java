package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.model.User;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction saveTransaction(Transaction transaction, Long userId) {
        User user = new User();
        user.setId(userId);
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }


    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    public Transaction updateTransaction(Long transactionId, Transaction transaction, Long userId) {
        // Query by both ID and userId in one query - more secure and efficient
        Transaction savedTransaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found or you don't have permission to access it."
                ));

        // Don't update date field as it's @CreationTimestamp with updatable=false
        savedTransaction.setAmount(transaction.getAmount());
        // savedTransaction.setDate(transaction.getDate()); // REMOVED - date shouldn't be updated
        savedTransaction.setAccount(transaction.getAccount());
        savedTransaction.setType(transaction.getType());
        savedTransaction.setCategory(transaction.getCategory());
        return transactionRepository.save(savedTransaction);
    }


    public void deleteTransaction(Long transactionId, Long userId) {
        // Query by both ID and userId - returns empty if not owned by user
        Transaction savedTransaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found or you don't have permission to delete it."
                ));

        transactionRepository.delete(savedTransaction);
    }
}