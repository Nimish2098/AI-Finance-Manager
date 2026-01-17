package com.project.financeApp.model.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budgets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private BigDecimal budgetAmount;

    private int month;
    private int year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
