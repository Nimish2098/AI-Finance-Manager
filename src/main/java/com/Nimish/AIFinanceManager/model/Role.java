package com.Nimish.AIFinanceManager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor  // Hibernate-friendly no-args constructor
@AllArgsConstructor // optional, if you want to create Role objects easily
@Builder            // optional, for builder pattern
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
