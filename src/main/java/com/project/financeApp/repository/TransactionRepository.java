package com.project.financeApp.repository;


import com.project.financeApp.model.entity.Transaction;
import com.project.financeApp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUser(User user);
    List<Transaction> findAllByUser(User user);

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<Transaction> findByUser(User user, Pageable pageable);

    
   // For cash flow - get transactions for a month ordered by date
    List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateAsc(
        User user,
        LocalDate startDate,
        LocalDate endDate
    );
    
}

