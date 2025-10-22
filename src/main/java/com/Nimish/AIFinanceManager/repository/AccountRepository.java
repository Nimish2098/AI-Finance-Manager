package com.Nimish.AIFinanceManager.repository;

import com.Nimish.AIFinanceManager.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
