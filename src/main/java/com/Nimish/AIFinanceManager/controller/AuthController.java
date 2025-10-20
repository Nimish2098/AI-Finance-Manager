package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.dto.LoginRequest;
import com.Nimish.AIFinanceManager.dto.RegisterRequest;
import com.Nimish.AIFinanceManager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService userService;


    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req){
            return userService.register(req);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req){
        String token = userService.login(req);
        return token;
    }
}
