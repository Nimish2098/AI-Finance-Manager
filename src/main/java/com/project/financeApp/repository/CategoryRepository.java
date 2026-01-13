package com.project.financeApp.repository;



import com.project.financeApp.model.entity.Category;
import com.project.financeApp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(UUID id, User user);
}
