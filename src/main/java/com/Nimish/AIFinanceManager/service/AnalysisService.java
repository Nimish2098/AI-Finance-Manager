package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final TransactionService transactionService;

    public Map<String,Object> getSummary(Long userId){
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);

        double totalIncome = transactions
                .stream().filter(t->t.getType().equalsIgnoreCase("Income"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t->t.getType().equalsIgnoreCase("Expense"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double savings = totalIncome-totalExpense;
        double savingsPercent = totalIncome==0?0:(savings/totalIncome)*100;

        Map<String,Double> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String,Object>summary = new HashMap<>();

        summary.put("totalIncome",totalIncome);
        summary.put("totalExpense",totalExpense);
        summary.put("savings",savings);
        summary.put("savingPercent", Math.round(savingsPercent*100.0)/100.0);
        summary.put("categoryTotals",categoryTotals);

        return summary;
    }

    public Map<String,String> getInsights(Long userId){
        Map<String,Object> summary = getSummary(userId);
        double savingsPercent = (double) summary.get("savingPercent");

        String message;
        if (savingsPercent < 20) {
            message = "You’re spending too much — try to cut down on non-essential categories.";
        } else if (savingsPercent < 40) {
            message = "Good balance, but still room for more savings.";
        } else {
            message = "Excellent! You’re maintaining healthy financial habits.";
        }

        return Map.of("insight", message);
    }
}
