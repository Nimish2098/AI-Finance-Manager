package com.project.financeApp.repository;


import com.project.financeApp.model.entity.Transaction;
import com.project.financeApp.model.dto.TrendDataDTO;
import com.project.financeApp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.YearMonth;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserAndTransactionDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    Page<Transaction> findByUser(User user, Pageable pageable);

    // For daily trends
            @Query("""
                SELECT new com.project.financeApp.model.dto.TrendDataDTO(
                    CAST(t.transactionDate AS string),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0)
                )
                FROM Transaction t
                WHERE t.user = :user 
                    AND YEAR(t.transactionDate) = :year 
                    AND MONTH(t.transactionDate) = :month
                GROUP BY t.transactionDate
                ORDER BY t.transactionDate
                """)
            List<TrendDataDTO> findDailyTrendsByUserAndYearAndMonth(
                @Param("user") User user,
                @Param("year") int year,
                @Param("month") int month
            );

            // For weekly trends
            @Query("""
                SELECT new com.project.financeApp.model.dto.TrendDataDTO(
                    CONCAT('Week ', CAST(WEEK(t.transactionDate, 1) - WEEK(:startDate, 1) + 1 AS string)),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0)
                )
                FROM Transaction t
                WHERE t.user = :user 
                    AND t.transactionDate >= :startDate 
                    AND t.transactionDate <= :endDate
                GROUP BY YEAR(t.transactionDate), WEEK(t.transactionDate, 1)
                ORDER BY YEAR(t.transactionDate), WEEK(t.transactionDate, 1)
                """)
            List<TrendDataDTO> findWeeklyTrendsByUserAndDateRange(
                @Param("user") User user,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
            );

            // For monthly trends
            @Query("""
                SELECT new com.project.financeApp.model.dto.TrendDataDTO(
                    FUNCTION('MONTHNAME', t.transactionDate),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0)
                )
                FROM Transaction t
                WHERE t.user = :user 
                    AND YEAR(t.transactionDate) = :year
                GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)
                ORDER BY MONTH(t.transactionDate)
                """)
            List<TrendDataDTO> findMonthlyTrendsByUserAndYear(
                @Param("user") User user,
                @Param("year") int year
            );

            // For cash flow - get transactions for a month ordered by date
            List<Transaction> findByUserAndTransactionDateBetweenOrderByTransactionDateAsc(
                User user,
                LocalDate startDate,
                LocalDate endDate
            );
    
}

