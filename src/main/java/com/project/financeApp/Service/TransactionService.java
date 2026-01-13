package com.project.financeApp.Service;

import com.project.financeApp.model.dto.DashboardSummaryDTO;
import com.project.financeApp.model.dto.MonthlySummaryDTO;
import com.project.financeApp.model.dto.TransactionRequestDTO;
import com.project.financeApp.model.dto.TransactionResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {

        TransactionResponseDTO createTransaction(TransactionRequestDTO request);

        List<TransactionResponseDTO> getAllTransactions();

        Page<TransactionResponseDTO> getTransactions(int page, int size);

        MonthlySummaryDTO getMonthlySummary(int month, int year);

        DashboardSummaryDTO getDashboardSummary(int month, int year);

        void exportTransactionsToCsv(HttpServletResponse response);

}

