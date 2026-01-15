package com.project.financeApp.repository;


import com.project.financeApp.model.entity.Budget;
import com.project.financeApp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    @Query("SELECT b FROM Budget b WHERE b.user = :user AND b.category.id = :categoryId AND b.month = :month AND b.year = :year")
    Optional<Budget> findByUserAndCategoryIdAndMonthAndYear(
            @Param("user") User user,
            @Param("categoryId") UUID categoryId,
            @Param("month") int month,
            @Param("year") int year
    );

    List<Budget> findByUser(User user);

    Optional<Budget> findByIdAndUser(UUID id, User user);

    List<Budget> findByUserAndMonthAndYear(User user, int month, int year);
}

