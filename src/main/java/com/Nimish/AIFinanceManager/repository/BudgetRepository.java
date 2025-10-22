package com.Nimish.AIFinanceManager.repository;

import com.Nimish.AIFinanceManager.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget,Long> {
}
