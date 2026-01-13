package com.project.financeApp.Service.impl;

import com.project.financeApp.Service.TransactionService;
import com.project.financeApp.Service.UserService;
import com.project.financeApp.mapper.TransactionMapper;
import com.project.financeApp.model.dto.*;
import com.project.financeApp.model.entity.*;
import com.project.financeApp.repository.AccountRepository;
import com.project.financeApp.repository.CategoryRepository;
import com.project.financeApp.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        User user = userService.getCurrentUser();

        Account account = accountRepository
                .findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Business rule: category & transaction type must match
        if (!category.getType().equals(request.type())) {
            throw new RuntimeException("Category type mismatch");
        }

        Transaction transaction = TransactionMapper.toEntity(
                request, user, account, category
        );

        updateAccountBalance(account, request);

        return TransactionMapper.toResponse(
                transactionRepository.save(transaction)
        );
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        User user = userService.getCurrentUser();

        return transactionRepository.findByUser(user)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    private void updateAccountBalance(Account account, TransactionRequestDTO request) {
        if (request.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(request.amount()));
        } else {
            account.setBalance(account.getBalance().add(request.amount()));
        }
    }

    @Override
    public Page<TransactionResponseDTO> getTransactions(int page, int size) {

        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("transactionDate").descending()
        );

        return transactionRepository.findByUser(user, pageable)
                .map(TransactionMapper::toResponse);
    }

    @Override
    public MonthlySummaryDTO getMonthlySummary(int month, int year) {

        User user = userService.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions =
                transactionRepository.findByUserAndTransactionDateBetween(
                        user, start, end
                );

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.getType() == TransactionType.INCOME) {
                income = income.add(tx.getAmount());
            } else {
                expense = expense.add(tx.getAmount());
            }
        }

        return new MonthlySummaryDTO(
                income,
                expense,
                income.subtract(expense)
        );
    }


    @Override
    public void exportTransactionsToCsv(HttpServletResponse response) {

        User user = userService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByUser(user);

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=transactions.csv"
        );

        try (PrintWriter writer = response.getWriter()) {

            writer.println("Date,Type,Amount,Category,Account,Description");

            for (Transaction tx : transactions) {
                writer.printf(
                        "%s,%s,%s,%s,%s,%s%n",
                        tx.getTransactionDate(),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getCategory().getName(),
                        tx.getAccount().getName(),
                        tx.getDescription() != null ? tx.getDescription() : ""
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV");
        }
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary(int month, int year) {

        User user = userService.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions =
                transactionRepository.findByUserAndTransactionDateBetween(
                        user, start, end
                );

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (Transaction tx : transactions) {

            if (tx.getType() == TransactionType.INCOME) {
                income = income.add(tx.getAmount());
            } else {
                expense = expense.add(tx.getAmount());

                categoryTotals.merge(
                        tx.getCategory().getName(),
                        tx.getAmount(),
                        BigDecimal::add
                );
            }
        }

        List<CategoryBreakdownDTO> breakdown =
                categoryTotals.entrySet()
                        .stream()
                        .map(e -> new CategoryBreakdownDTO(e.getKey(), e.getValue()))
                        .toList();

        return new DashboardSummaryDTO(
                income,
                expense,
                income.subtract(expense),
                breakdown
        );
    }


}

