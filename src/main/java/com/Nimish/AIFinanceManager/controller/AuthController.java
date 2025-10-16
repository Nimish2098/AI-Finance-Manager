package com.Nimish.AIFinanceManager.controller;

import com.Nimish.AIFinanceManager.dto.RegisterRequest;
import com.Nimish.AIFinanceManager.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;


    @PostMapping
    public ResponseEntity<String> register(@PathVariable  RegisterRequest request){

        
    }

}
