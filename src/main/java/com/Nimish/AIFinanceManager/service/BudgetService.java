package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Budget;
import com.Nimish.AIFinanceManager.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public List<Budget> getAllBudgets(){
        return budgetRepository.findAll();
    }

    public Budget saveBudget(Budget budget){
        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id){
        budgetRepository.deleteById(id);
    }

    public Budget updateBudget(Long id, Budget budget){
        Budget savedBudget = budgetRepository.findById(id).orElseThrow();
        savedBudget.setMonth(budget.getMonth());
        savedBudget.setCategory(budget.getCategory());
        savedBudget.setLimitAmount(budget.getLimitAmount());
        savedBudget.setMonth(budget.getMonth());
        return budgetRepository.save(savedBudget);
    }

}
