package com.project.financeApp.controller;


import com.project.financeApp.Service.impl.TransactionServiceImpl;
import com.project.financeApp.model.dto.DashboardSummaryDTO;
import com.project.financeApp.model.dto.MonthlySummaryDTO;
import com.project.financeApp.model.dto.TransactionRequestDTO;
import com.project.financeApp.model.dto.TransactionResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping
    public TransactionResponseDTO createTransaction(
            @RequestBody TransactionRequestDTO request
    ) {
        return transactionService.createTransaction(request);
    }



    @GetMapping("/paged")
    public Page<TransactionResponseDTO> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.getTransactions(page, size);
    }

    @GetMapping("/summary/monthly")
    public MonthlySummaryDTO getMonthlySummary(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return transactionService.getMonthlySummary(month, year);
    }

    @GetMapping("/export/csv")
    public void exportTransactions(HttpServletResponse response) {
        transactionService.exportTransactionsToCsv(response);
    }

    @GetMapping("/dashboard")
    public DashboardSummaryDTO getDashboard(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return transactionService.getDashboardSummary(month, year);
    }


}

