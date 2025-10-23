package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.service.AnalysisService;
import com.Nimish.AIFinanceManager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;

    private final AnalysisService analysisService;

    @PostMapping("/add/{id}")
    public String addTransaction(@RequestBody Transaction transaction, @PathVariable Long id){
        transactionService.saveTransaction(transaction,id);
        return "Transaction Added";
    }

    @GetMapping
    public List<Transaction> getAllTransactions(){
        return transactionService.getAllTransaction();
    }

    @PostMapping("/update/{id}")
    public String updateTransaction(@RequestBody Transaction transaction,@PathVariable Long id){
        transactionService.updateTransaction(id,transaction);
        return "Transaction Updated Succcessfully";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);
        return "Transaction deleted Sucessfully";
    }

    @GetMapping("/summary")
    public Map<String ,Object> getSummary(){
        return analysisService.getSummary();
    }

    @GetMapping("/insights")
    public Map<String,String> getInsights(){
        return analysisService.getInsights();
    }
}
