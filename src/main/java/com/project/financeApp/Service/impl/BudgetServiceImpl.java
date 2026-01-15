package com.project.financeApp.Service.impl;

import com.project.financeApp.Service.BudgetService;
import com.project.financeApp.Service.UserService;
import com.project.financeApp.mapper.BudgetMapper;
import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import com.project.financeApp.model.dto.BudgetStatusDTO;
import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.Transaction;
import com.project.financeApp.model.entity.TransactionType;
import com.project.financeApp.model.entity.User;
import com.project.financeApp.repository.BudgetRepository;
import com.project.financeApp.repository.CategoryRepository;
import com.project.financeApp.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BudgetResponseDTO createBudget(BudgetRequestDTO request) {
        User user = userService.getCurrentUser();

        // Validate month and year
        if (request.month() < 1 || request.month() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (request.year() < 1900 || request.year() > 2100) {
            throw new IllegalArgumentException("Invalid year");
        }

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if budget already exists for this category, month, and year
        budgetRepository.findByUserAndCategoryIdAndMonthAndYear(
                user, request.categoryId(), request.month(), request.year()
        ).ifPresent(budget -> {
            throw new RuntimeException(
                    "Budget already exists for category " + category.getName() +
                            " in " + getMonthName(request.month()) + " " + request.year()
            );
        });

        Budget budget = BudgetMapper.toEntity(request, user, category);
        Budget savedBudget = budgetRepository.save(budget);

        // Calculate spent amount (will be 0 for new budget)
        BigDecimal spentAmount = calculateSpentAmount(savedBudget, user);

        return BudgetMapper.toResponse(savedBudget, spentAmount);
    }

    @Override
    public List<BudgetResponseDTO> getAllBudgets() {
        User user = userService.getCurrentUser();
        return budgetRepository.findByUser(user)
                .stream()
                .map(budget -> {
                    BigDecimal spentAmount = calculateSpentAmount(budget, user);
                    return BudgetMapper.toResponse(budget, spentAmount);
                })
                .toList();
    }

    @Override
    public BudgetResponseDTO getBudgetById(UUID budgetId) {
        User user = userService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        BigDecimal spentAmount = calculateSpentAmount(budget, user);
        return BudgetMapper.toResponse(budget, spentAmount);
    }

    @Override
    @Transactional
    public BudgetResponseDTO updateBudget(UUID budgetId, BudgetRequestDTO request) {
        User user = userService.getCurrentUser();

        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        // Validate month and year
        if (request.month() < 1 || request.month() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (request.year() < 1900 || request.year() > 2100) {
            throw new IllegalArgumentException("Invalid year");
        }

        // If category, month, or year changed, check for duplicates
        if (!budget.getCategory().getId().equals(request.categoryId()) ||
                budget.getMonth() != request.month() ||
                budget.getYear() != request.year()) {

            budgetRepository.findByUserAndCategoryIdAndMonthAndYear(
                    user, request.categoryId(), request.month(), request.year()
            ).ifPresent(existingBudget -> {
                if (!existingBudget.getId().equals(budgetId)) {
                    throw new RuntimeException(
                            "Budget already exists for this category, month, and year"
                    );
                }
            });
        }

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        budget.setLimitAmount(request.limitAmount());
        budget.setMonth(request.month());
        budget.setYear(request.year());
        budget.setCategory(category);

        Budget savedBudget = budgetRepository.save(budget);
        BigDecimal spentAmount = calculateSpentAmount(savedBudget, user);

        return BudgetMapper.toResponse(savedBudget, spentAmount);
    }

    @Override
    @Transactional
    public void deleteBudget(UUID budgetId) {
        User user = userService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        budgetRepository.delete(budget);
    }

    @Override
    public List<BudgetResponseDTO> getBudgetsByMonthAndYear(int month, int year) {
        User user = userService.getCurrentUser();
        return budgetRepository.findByUserAndMonthAndYear(user, month, year)
                .stream()
                .map(budget -> {
                    BigDecimal spentAmount = calculateSpentAmount(budget, user);
                    return BudgetMapper.toResponse(budget, spentAmount);
                })
                .toList();
    }

    @Override
    public BudgetStatusDTO getBudgetStatus(UUID budgetId) {
        User user = userService.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        BigDecimal spentAmount = calculateSpentAmount(budget, user);
        BigDecimal remainingAmount = budget.getLimitAmount().subtract(spentAmount);
        boolean isExceeded = spentAmount.compareTo(budget.getLimitAmount()) > 0;

        // Calculate percentage used
        double percentageUsed = 0.0;
        if (budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0) {
            percentageUsed = spentAmount
                    .divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return new BudgetStatusDTO(
                budget.getId(),
                budget.getCategory().getName(),
                budget.getLimitAmount(),
                spentAmount,
                remainingAmount,
                percentageUsed,
                isExceeded,
                budget.getMonth(),
                budget.getYear()
        );
    }

    /**
     * Helper method to calculate spent amount for a budget
     */
    private BigDecimal calculateSpentAmount(Budget budget, User user) {
        // Calculate date range for the budget month
        LocalDate start = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Get all expense transactions for this category in the budget month
        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end);

        // Calculate spent amount for this category
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> t.getCategory().getId().equals(budget.getCategory().getId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getMonthName(int month) {
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1];
    }
}