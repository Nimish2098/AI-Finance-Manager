package com.project.financeApp.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;        // OVESPENDING, SPIKE, SUGGESTION
    private String category;    // Food, Shopping, etc.
    private Double amount;      // excess / increase
    private String message;     // user-facing insight text

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
