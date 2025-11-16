package com.Nimish.AIFinanceManager.repository;

import com.Nimish.AIFinanceManager.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Transaction getTransactionById(Long id);
    List<Transaction> findByUserId(Long userId);
}
