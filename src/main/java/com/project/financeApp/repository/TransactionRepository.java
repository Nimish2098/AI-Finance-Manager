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
                FUNCTION('DATE_FORMAT', t.transactionDate, '%Y-%m-%d'),
                SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),
                SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),
                SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END)
            )
            FROM Transaction t
            WHERE t.user = :user
            AND FUNCTION('YEAR', t.transactionDate) = :year
            AND FUNCTION('MONTH', t.transactionDate) = :month
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
                    CONCAT(
                        'Week ',
                        FUNCTION('WEEK', t.transactionDate) - FUNCTION('WEEK', :startDate) + 1
                    ),
                    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),
                    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),
                    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END)
                )
                FROM Transaction t
                WHERE t.user = :user
                  AND t.transactionDate BETWEEN :startDate AND :endDate
                GROUP BY FUNCTION('YEAR', t.transactionDate),
                         FUNCTION('WEEK', t.transactionDate)
                ORDER BY FUNCTION('YEAR', t.transactionDate),
                         FUNCTION('WEEK', t.transactionDate)
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
                    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),
                    SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),
                    SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END)
                )
                FROM Transaction t
                WHERE t.user = :user
                AND FUNCTION('YEAR', t.transactionDate) = :year
                GROUP BY FUNCTION('YEAR', t.transactionDate),
                        FUNCTION('MONTH', t.transactionDate)
                ORDER BY FUNCTION('MONTH', t.transactionDate)
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

