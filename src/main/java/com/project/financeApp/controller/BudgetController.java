package com.project.financeApp.controller;


import com.project.financeApp.Service.BudgetService;
import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public BudgetResponseDTO createBudget(
            @RequestBody BudgetRequestDTO request
    ) {
        return budgetService.createBudget(request);
    }
}

