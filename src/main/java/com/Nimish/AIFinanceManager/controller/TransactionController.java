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
@CrossOrigin(origins = "*")
public class TransactionController {


    private final TransactionService transactionService;

    private final AnalysisService analysisService;

    @PostMapping("/{userId}")
    public String addTransaction(@RequestBody Transaction transaction, @PathVariable Long id){
        transactionService.saveTransaction(transaction,id);
        return "Transaction Added";
    }

    @GetMapping
    public List<Transaction> getTransactionsByUser(@PathVariable Long id){
        return transactionService.getTransactionsByUser(id);
    }

    @PostMapping("/{id}")
    public String updateTransaction(@RequestBody Transaction transaction,@PathVariable Long id){
        transactionService.updateTransaction(id,transaction);
        return "Transaction Updated Succcessfully";
    }

    @DeleteMapping("/delete/{userId}")
    public String deleteTransaction(@PathVariable Long userId){
        transactionService.deleteTransaction(userId);
        return "Transaction deleted Sucessfully";
    }

    @GetMapping("/summary/{userId}")
    public Map<String ,Object> getSummary(@PathVariable Long userId){
        return analysisService.getSummary(userId);
    }

    @GetMapping("/insights/{userId}")
    public Map<String,String> getInsights(@PathVariable Long userId){
        return analysisService.getInsights(userId);
    }
}
