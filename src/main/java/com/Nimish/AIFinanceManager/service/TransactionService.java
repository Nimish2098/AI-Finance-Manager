package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.model.Account;
import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.repository.AccountRepository;
import com.Nimish.AIFinanceManager.repository.FinancialInsightRepository;
import com.Nimish.AIFinanceManager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;
    private FinancialInsightRepository financialInsightRepository;



    @Transactional
    public Transaction saveTransaction(Transaction transaction, Long accountId) {
        // 1️⃣ Find the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        transaction.setAccount(account);

        // 2️⃣ Update balance based on transaction type
        if (transaction.getType().equalsIgnoreCase("Income")) {
            account.setBalance(account.getBalance() + transaction.getAmount());
        } else if (transaction.getType().equalsIgnoreCase("Expense")) {
            account.setBalance(account.getBalance() - transaction.getAmount());
        }

        accountRepository.save(account);

        String flaskUrl = "http://localhost:5000/predict";
        Map<String, Object> request = new HashMap<>();
        request.put("amount", transaction.getAmount());
        request.put("type", transaction.getType());
        request.put("category", transaction.getCategory());
        request.put("balance", account.getBalance());

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(flaskUrl, request, Map.class);

        // 5️⃣ Handle AI prediction
        if (response != null && response.get("prediction") != null) {
            FinancialInsight insight = new FinancialInsight();
            insight.setMessage(response.get("prediction").toString());
            insight.setAmount(transaction.getAmount());
            insight.setType(transaction.getType());
            insight.setCategory(transaction.getCategory());
            insight.setAccount(account);
            insight.setTransaction(transaction);

            financialInsightRepository.save(insight);
            System.out.println("AI Insight saved: " + insight.getMessage());
        }

        // 6️⃣ Finally, save transaction
        return transactionRepository.save(transaction);
    }



    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }

    public String updateTransaction(Long transactionId,Transaction transaction){
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        savedTransaction.setAmount(transaction.getAmount());
        savedTransaction.setDate(transaction.getDate());
        savedTransaction.setType(transaction.getType());
        savedTransaction.setCategory(transaction.getCategory());
        transactionRepository.save(savedTransaction);
        return "Transaction updated Successfully";
    }

    public String deleteTransaction(Long transactionId){
        Transaction savedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(()->new RuntimeException("Transaction not found."));
        transactionRepository.delete(savedTransaction);
        return "Transaction deleted Successfully";
    }
}
