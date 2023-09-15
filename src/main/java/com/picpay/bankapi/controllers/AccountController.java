package com.picpay.bankapi.controllers;

import com.picpay.bankapi.entities.Account;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.picpay.bankapi.services.AccountService;
import com.picpay.bankapi.controllers.DTOs.NewUserDTO;

@Controller
@AllArgsConstructor
@RequestMapping("/api/accounts/v1")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody NewUserDTO params) {
        var account = accountService.create(params);
        var accountPath = "/api/account/v1/" + account.getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountPath);
    }
}
