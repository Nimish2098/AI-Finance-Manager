package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.dto.LoginRequest;
import com.Nimish.AIFinanceManager.dto.RegisterRequest;
import com.Nimish.AIFinanceManager.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req){
            return authService.register(req);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req){
        String token = authService.login(req);
        return token;
    }
}
