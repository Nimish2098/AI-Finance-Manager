package com.project.financeApp.repository;


import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Optional<Budget> findByUserAndCategoryIdAndMonthAndYear(
            User user,
            UUID categoryId,
            int month,
            int year
    );
}

