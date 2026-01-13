package com.project.financeApp.Service;

import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;

public interface BudgetService {

    BudgetResponseDTO createBudget(BudgetRequestDTO request);
}

