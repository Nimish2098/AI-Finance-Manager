package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Budget;
import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.repository.BudgetRepository;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public List<Budget> getAllBudgets(){
        List<Budget> budgets = budgetRepository.findAll();
        // Calculate spent for each budget
        return budgets.stream()
                .map(this::calculateSpent)
                .collect(Collectors.toList());
    }

    public Budget saveBudget(Budget budget){
        Budget saved = budgetRepository.save(budget);
        return calculateSpent(saved);
    }

    public void deleteBudget(Long id){
        budgetRepository.deleteById(id);
    }

    public Budget updateBudget(Long id, Budget budget){
        Budget savedBudget = budgetRepository.findById(id).orElseThrow();
        savedBudget.setMonth(budget.getMonth());
        savedBudget.setCategory(budget.getCategory());
        savedBudget.setLimit(budget.getLimit());
        savedBudget.setName(budget.getName());
        Budget updated = budgetRepository.save(savedBudget);
        return calculateSpent(updated);
    }

    // Helper method to calculate spent amount from transactions
    private Budget calculateSpent(Budget budget) {
        // Get all transactions for this category
        List<Transaction> transactions = transactionRepository.findAll()
                .stream()
                .filter(t -> t.getCategory() != null &&
                        t.getCategory().equals(budget.getCategory()) &&
                        t.getType() != null &&
                        t.getType().equalsIgnoreCase("EXPENSE"))
                .collect(java.util.stream.Collectors.toList());

        // Filter by month if budget has a month set
        if (budget.getMonth() != null && !budget.getMonth().isEmpty()) {
            transactions = transactions.stream()
                    .filter(t -> t.getDate() != null &&
                            t.getDate().startsWith(budget.getMonth().substring(0, 7))) // Match YYYY-MM format
                    .collect(java.util.stream.Collectors.toList());
        }

        // Calculate total spent
        double totalSpent = transactions.stream()
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0)
                .sum();

        budget.setSpent(totalSpent);
        return budget;
    }

}