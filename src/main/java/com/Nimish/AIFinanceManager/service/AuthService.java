package com.Nimish.AIFinanceManager.service;

import com.Nimish.AIFinanceManager.config.JwtUtil;
import com.Nimish.AIFinanceManager.dto.LoginRequest;
import com.Nimish.AIFinanceManager.dto.RegisterRequest;
import com.Nimish.AIFinanceManager.model.User;
import com.Nimish.AIFinanceManager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email Already in Use");
        }

        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        return "User Registered Successfully";
    }
    public String login(LoginRequest loginRequest){
        Optional<User> user =userRepository.findByEmail(loginRequest.getEmail());
        if(user.isEmpty()|| !passwordEncoder.matches(loginRequest.getPassword(),user.get().getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }
        return jwtUtil.generateToken(loginRequest.getEmail());
    }



}
