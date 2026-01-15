package com.project.financeApp.controller;


import com.project.financeApp.Service.BudgetService;
import com.project.financeApp.model.dto.BudgetRequestDTO;
import com.project.financeApp.model.dto.BudgetResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.project.financeApp.model.dto.BudgetStatusDTO;
import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public List<BudgetResponseDTO> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/{id}")
    public BudgetResponseDTO getBudgetById(@PathVariable UUID id) {
        return budgetService.getBudgetById(id);
    }

    @PutMapping("/{id}")
    public BudgetResponseDTO updateBudget(
            @PathVariable UUID id,
            @RequestBody BudgetRequestDTO request
    ) {
        return budgetService.updateBudget(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
    }

    @GetMapping("/month")
    public List<BudgetResponseDTO> getBudgetsByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return budgetService.getBudgetsByMonthAndYear(month, year);
    }

    @GetMapping("/{id}/status")
    public BudgetStatusDTO getBudgetStatus(@PathVariable UUID id) {
        return budgetService.getBudgetStatus(id);
    }
}

