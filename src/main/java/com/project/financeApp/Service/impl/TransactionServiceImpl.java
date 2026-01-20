package com.project.financeApp.Service.impl;

import com.project.financeApp.Service.TransactionService;
import com.project.financeApp.Service.UserService;
import com.project.financeApp.mapper.TransactionMapper;
import com.project.financeApp.model.dto.*;
import com.project.financeApp.model.entity.*;
import com.project.financeApp.repository.AccountRepository;
import com.project.financeApp.repository.CategoryRepository;
import com.project.financeApp.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;


    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        User user = userService.getCurrentUser();

        Account account = accountRepository
                .findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Category category = categoryRepository
                .findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Business rule: category & transaction type must match
        if (!category.getType().equals(request.type())) {
            throw new RuntimeException("Category type mismatch");
        }

        Transaction transaction = TransactionMapper.toEntity(
                request, user, account, category
        );

        updateAccountBalance(account, request);

        return TransactionMapper.toResponse(
                transactionRepository.save(transaction)
        );
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        User user = userService.getCurrentUser();

        return transactionRepository.findByUser(user)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    private void updateAccountBalance(Account account, TransactionRequestDTO request) {
        if (request.type() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(request.amount()));
        } else {
            account.setBalance(account.getBalance().add(request.amount()));
        }
    }

    @Override
    public Page<TransactionResponseDTO> getTransactions(int page, int size) {

        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("transactionDate").descending()
        );

        return transactionRepository.findByUser(user, pageable)
                .map(TransactionMapper::toResponse);
    }

    @Override
    public MonthlySummaryDTO getMonthlySummary(int month, int year) {

        User user = userService.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions =
                transactionRepository.findByUserAndTransactionDateBetween(
                        user, start, end
                );

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.getType() == TransactionType.INCOME) {
                income = income.add(tx.getAmount());
            } else {
                expense = expense.add(tx.getAmount());
            }
        }

        return new MonthlySummaryDTO(
                income,
                expense,
                income.subtract(expense)
        );
    }


    @Override
    public void exportTransactionsToCsv(HttpServletResponse response) {

        User user = userService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByUser(user);

        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=transactions.csv"
        );

        try (PrintWriter writer = response.getWriter()) {

            writer.println("Date,Type,Amount,Category,Account,Description");

            for (Transaction tx : transactions) {
                writer.printf(
                        "%s,%s,%s,%s,%s,%s%n",
                        tx.getTransactionDate(),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getCategory().getName(),
                        tx.getAccount().getName(),
                        tx.getDescription() != null ? tx.getDescription() : ""
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV");
        }
    }

    @Override
    public DashboardSummaryDTO getDashboardSummary(int month, int year) {

        User user = userService.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions =
                transactionRepository.findByUserAndTransactionDateBetween(
                        user, start, end
                );

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        for (Transaction tx : transactions) {

            if (tx.getType() == TransactionType.INCOME) {
                income = income.add(tx.getAmount());
            } else {
                expense = expense.add(tx.getAmount());

                categoryTotals.merge(
                        tx.getCategory().getName(),
                        tx.getAmount(),
                        BigDecimal::add
                );
            }
        }

        List<CategoryBreakdownDTO> breakdown =
                categoryTotals.entrySet()
                        .stream()
                        .map(e -> new CategoryBreakdownDTO(e.getKey(), e.getValue()))
                        .toList();

        return new DashboardSummaryDTO(
                income,
                expense,
                income.subtract(expense),
                breakdown
        );
    }
    @Cacheable(value = "trends", key = "#period + '_' + #year + '_' + (#month != null ? #month : 'all')")
    @Override
    public List<TrendDataDTO> getSpendingTrends(String period, int year, Integer month) {
        User user = userService.getCurrentUser();

        switch (period.toLowerCase()) {
            case "daily" -> {
                if (month == null) {
                    throw new IllegalArgumentException("Month is required for daily trends");
                }
                return getDailyTrends(user, year, month);
            }
            case "weekly" -> {
                if (month == null) {
                    throw new IllegalArgumentException("Month is required for weekly trends");
                }
                return getWeeklyTrends(user, year, month);
            }
            case "monthly" -> {
                return getMonthlyTrends(user, year);
            }
            default -> throw new IllegalArgumentException("Invalid period. Use: daily, weekly, or monthly");
        }
    }

    private List<TrendDataDTO> getDailyTrends(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end);

        // Group by date
        Map<LocalDate, List<Transaction>> byDate = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionDate));

        List<TrendDataDTO> trends = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<Transaction> dayTransactions = byDate.getOrDefault(date, List.of());

            BigDecimal income = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trends.add(new TrendDataDTO(
                    date.toString(),
                    income,
                    expense,
                    income.subtract(expense)
            ));
        }
        return trends;
    }

    private List<TrendDataDTO> getWeeklyTrends(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end);

        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Group by week
        Map<Integer, List<Transaction>> byWeek = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().get(weekFields.weekOfYear())
                ));

        int firstWeek = start.get(weekFields.weekOfYear());
        int lastWeek = end.get(weekFields.weekOfYear());

        List<TrendDataDTO> trends = new ArrayList<>();
        for (int week = firstWeek; week <= lastWeek; week++) {
            List<Transaction> weekTransactions = byWeek.getOrDefault(week, List.of());

            BigDecimal income = weekTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = weekTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trends.add(new TrendDataDTO(
                    "Week " + (week - firstWeek + 1),
                    income,
                    expense,
                    income.subtract(expense)
            ));
        }
        return trends;
    }

    private List<TrendDataDTO> getMonthlyTrends(User user, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end);

        // Group by month
        Map<Integer, List<Transaction>> byMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTransactionDate().getMonthValue()));

        List<TrendDataDTO> trends = new ArrayList<>();
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        for (int month = 1; month <= 12; month++) {
            List<Transaction> monthTransactions = byMonth.getOrDefault(month, List.of());

            BigDecimal income = monthTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expense = monthTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trends.add(new TrendDataDTO(
                    monthNames[month - 1],
                    income,
                    expense,
                    income.subtract(expense)
            ));
        }
        return trends;
    }
    @Cacheable(value = "cashflow", key = "#month + '_' + #year")
    @Override
    public CashFlowDTO getCashFlowAnalysis(int month, int year) {
        User user = userService.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Get opening balance (sum of all account balances before this month)
        LocalDate beforeMonth = start.minusDays(1);
        List<Transaction> beforeTransactions = transactionRepository
                .findByUserAndTransactionDateBetween(
                        user,
                        LocalDate.of(1900, 1, 1), // Start from beginning, or track initial balances
                        beforeMonth
                );

        // Calculate opening balance from all accounts
        BigDecimal openingBalance = accountRepository.findByUser(user).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Subtract transactions before this month to get opening balance
        for (Transaction tx : beforeTransactions) {
            if (tx.getType() == TransactionType.INCOME) {
                openingBalance = openingBalance.subtract(tx.getAmount());
            } else {
                openingBalance = openingBalance.add(tx.getAmount());
            }
        }

        // Get transactions for this month, ordered by date
        List<Transaction> monthTransactions = transactionRepository
                .findByUserAndTransactionDateBetween(user, start, end)
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();

        BigDecimal totalInflow = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutflow = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build daily flow
        Map<LocalDate, List<Transaction>> byDate = monthTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getTransactionDate));

        List<DailyCashFlowDTO> dailyFlow = new ArrayList<>();
        BigDecimal runningBalance = openingBalance;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            List<Transaction> dayTransactions = byDate.getOrDefault(date, List.of());

            BigDecimal dayInflow = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal dayOutflow = dayTransactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            runningBalance = runningBalance.add(dayInflow).subtract(dayOutflow);

            dailyFlow.add(new DailyCashFlowDTO(
                    date.toString(),
                    dayInflow,
                    dayOutflow,
                    runningBalance
            ));
        }

        BigDecimal closingBalance = runningBalance;

        return new CashFlowDTO(
                openingBalance,
                closingBalance,
                totalInflow,
                totalOutflow,
                dailyFlow
        );
    }




    public ByteArrayInputStream exportTransactions(String format) throws IOException {
        User user = userService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(transactions);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            return exportToXlsx(transactions);
        } else {
            throw new IllegalArgumentException("Unsupported export format");
        }
    }

    private ByteArrayInputStream exportToCsv(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Amount,Type,Category,Account,Description\n");

        for (Transaction t : transactions) {
            sb.append(t.getTransactionDate()).append(",")
                    .append(t.getAmount()).append(",")
                    .append(t.getType()).append(",")
                    .append(t.getCategory() != null ? t.getCategory().getName() : "").append(",")
                    .append(t.getAccount() != null ? t.getAccount().getName() : "").append(",")
                    .append(t.getDescription() != null ? t.getDescription() : "")
                    .append("\n");
        }

        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private ByteArrayInputStream exportToXlsx(List<Transaction> transactions) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row header = sheet.createRow(0);
        String[] columns = {"Date", "Amount", "Type", "Category", "Account", "Description"};

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        for (Transaction t : transactions) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(t.getTransactionDate().toString());
            row.createCell(1).setCellValue(t.getAmount().doubleValue());
            row.createCell(2).setCellValue(t.getType().name());
            row.createCell(3).setCellValue(t.getCategory() != null ? t.getCategory().getName() : "");
            row.createCell(4).setCellValue(t.getAccount() != null ? t.getAccount().getName() : "");
            row.createCell(5).setCellValue(t.getDescription() != null ? t.getDescription() : "");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }



    @Transactional
    public void importFromCsv(MultipartFile file) {
        User user = userService.getCurrentUser();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean skipHeader = true;

            while ((line = reader.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                // LIMIT split to 6 columns (prevents comma issues in description)
                String[] data = line.split(",", 6);

                Transaction transaction = Transaction.builder()
                        .transactionDate(LocalDate.parse(data[0].trim()))
                        .amount(new BigDecimal(data[1].trim()))
                        .type(TransactionType.valueOf(data[2].trim().toUpperCase()))
                        .category(findOrCreateCategory(data[3], user))
                        .account(findOrCreateAccount(data[4], user))
                        .description(data.length == 6 ? data[5].trim() : null)
                        .user(user)
                        .build();

                transactionRepository.save(transaction);
            }

        } catch (Exception e) {
            e.printStackTrace(); // KEEP THIS while debugging
            throw new RuntimeException("Failed to import CSV file", e);
        }
    }

    private Category findOrCreateCategory(String name, User user) {
        if (name == null || name.trim().isEmpty()) return null;

        return categoryRepository.findByNameAndUser(name.trim(), user)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .name(name.trim())
                                .user(user)
                                .build()
                ));
    }
    private Account findOrCreateAccount(String name, User user) {
        if (name == null || name.trim().isEmpty()) return null;

        return accountRepository.findByNameAndUser(name.trim(), user)
                .orElseGet(() -> accountRepository.save(
                        Account.builder()
                                .name(name.trim())
                                .user(user)
                                .build()
                ));
    }

    private Category findCategory(String name, User user) {
        if (name == null || name.isBlank()) return null;
        return categoryRepository.findByNameAndUser(name, user)
                .orElse(null);
    }

    private Account findAccount(String name, User user) {
        if (name == null || name.isBlank()) return null;
        return accountRepository.findByNameAndUser(name, user)
                .orElse(null);
    }

}

