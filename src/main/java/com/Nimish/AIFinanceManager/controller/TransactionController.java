package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.model.Transaction;
import com.Nimish.AIFinanceManager.service.AnalysisService;
import com.Nimish.AIFinanceManager.service.TransactionService;
import com.Nimish.AIFinanceManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AnalysisService analysisService;
    private final UserRepository userRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction, @PathVariable Long userId){
        Transaction saved = transactionService.saveTransaction(transaction,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Transaction> getTransactionsByUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return transactionService.getTransactionsByUser(userId);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Transaction>updateTransaction(@RequestBody Transaction transaction,@PathVariable Long id){
        Transaction updatedTransaction = transactionService.updateTransaction(id,transaction);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTransaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable Long transactionId) {

        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Deleted")
        );
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