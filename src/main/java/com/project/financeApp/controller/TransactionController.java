package com.project.financeApp.controller;


import com.project.financeApp.Service.impl.TransactionServiceImpl;
import com.project.financeApp.model.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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


    @GetMapping
    public List<TransactionResponseDTO> getAllTransactions(
            @RequestBody TransactionRequestDTO responseDTO
    ){
        return transactionService.getAllTransactions();
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


    @GetMapping("/trends")
    public List<TrendDataDTO> getSpendingTrends(
            @RequestParam String period,  // "daily", "weekly", "monthly"
            @RequestParam int year,
            @RequestParam(required = false) Integer month  // only for daily/weekly
    ) {
        return transactionService.getSpendingTrends(period, year, month);
    }

    @GetMapping("/cashflow")
    public CashFlowDTO getCashFlowAnalysis(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return transactionService.getCashFlowAnalysis(month, year);
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportTransactions(
            @RequestParam(defaultValue = "xlsx") String format) throws IOException {

        ByteArrayInputStream stream = transactionService.exportTransactions(format);

        String fileName = "transactions." + format.toLowerCase();
        String contentType = format.equalsIgnoreCase("csv")
                ? "text/csv"
                : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(stream));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importTransactions(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.badRequest().body("Invalid file. Please upload a CSV file.");
        }

        transactionService.importFromCsv(file);
        return ResponseEntity.ok("Transactions imported successfully");
    }
}

