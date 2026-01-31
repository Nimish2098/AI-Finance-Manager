package com.project.financeApp.Service;


import com.project.financeApp.model.entity.User;

public interface UserService {

    User getCurrentUser();

    User updateUser(Long id);
}

