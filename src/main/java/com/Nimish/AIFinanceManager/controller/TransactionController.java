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

    // Helper method to get current authenticated user's ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }


    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction){
        Long userId = getCurrentUserId();
        Transaction saved = transactionService.saveTransaction(transaction,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Transaction> getTransactionsByUser() {
        Long userId = getCurrentUserId();
        return transactionService.getTransactionsByUser(userId);
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody Transaction transaction, @PathVariable Long id) {
        Long userId = getCurrentUserId();
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction, userId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTransaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(@PathVariable Long transactionId) {
        Long userId = getCurrentUserId();
        transactionService.deleteTransaction(transactionId, userId);
        return ResponseEntity.ok(
                Map.of("success", true, "message", "Deleted")
        );
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Long userId = getCurrentUserId();
        return analysisService.getSummary(userId);
    }

    @GetMapping("/insights")
    public Map<String, String> getInsights() {
        Long userId = getCurrentUserId();
        return analysisService.getInsights(userId);
    }
}