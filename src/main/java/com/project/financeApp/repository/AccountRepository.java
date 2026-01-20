package com.project.financeApp.repository;


import com.project.financeApp.model.entity.Account;
import com.project.financeApp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUser(User user);

    Optional<Account> findByIdAndUser(UUID id, User user);
    Optional<Account> findByNameAndUser(String name, User user);

}

