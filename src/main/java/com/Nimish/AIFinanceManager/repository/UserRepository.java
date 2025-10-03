package com.Nimish.AIFinanceManager.repository;

import com.Nimish.AIFinanceManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
