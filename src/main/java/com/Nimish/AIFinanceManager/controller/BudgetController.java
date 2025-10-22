package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.model.Budget;
import com.Nimish.AIFinanceManager.repository.BudgetRepository;
import com.Nimish.AIFinanceManager.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget")
public class BudgetController {

   private final BudgetService budgetService;

   @GetMapping
    public List<Budget> getAllBudget(){
       return budgetService.getAllBudgets();
   }

   @PostMapping("/add")
    public Budget addBudget(Budget budget){
       return budgetService.saveBudget(budget);
   }

   @PostMapping("/update/{id}")
    public Budget updateBudget(@RequestBody Budget budget,@PathVariable Long id){
       return budgetService.updateBudget(id,budget);
   }

   @DeleteMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id){
       budgetService.deleteBudget(id);
       return "Budget deleted Successfully";
   }
}
