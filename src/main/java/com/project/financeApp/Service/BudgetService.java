package com.project.financeApp.Service;

import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import com.project.financeApp.model.dto.BudgetStatusDTO;

import java.util.List;
import java.util.UUID;

public interface BudgetService {

    BudgetResponseDTO createBudget(BudgetRequestDTO request);

    List<BudgetResponseDTO> getAllBudgets();

    BudgetResponseDTO getBudgetById(UUID budgetId);

    BudgetResponseDTO updateBudget(UUID budgetId, BudgetRequestDTO request);

    void deleteBudget(UUID budgetId);

    List<BudgetResponseDTO> getBudgetsByMonthAndYear(int month, int year);

    BudgetStatusDTO getBudgetStatus(UUID budgetId);
}

