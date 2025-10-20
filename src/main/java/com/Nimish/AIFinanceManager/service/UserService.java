package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.repository.RoleRepository;
import com.Nimish.AIFinanceManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository repository;
    public final BCryptPasswordEncoder encoder;


}
