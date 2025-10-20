package com.Nimish.AIFinanceManager.repository;

import com.Nimish.AIFinanceManager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String name);
}