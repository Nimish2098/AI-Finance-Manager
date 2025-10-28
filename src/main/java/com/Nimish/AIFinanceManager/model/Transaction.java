package com.Nimish.AIFinanceManager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String type;
    private Double amount;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference
    private Account account;

    @Column(name="ai_label")
    private String aiLabel;
}
