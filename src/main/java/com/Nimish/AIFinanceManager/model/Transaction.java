package com.Nimish.AIFinanceManager.model;

import java.time.LocalDateTime;

public class Transaction {

    private Long id;
    private Long accountId;
    private String description;
    private Double amount;
    private LocalDateTime date;
    private String category;
}
