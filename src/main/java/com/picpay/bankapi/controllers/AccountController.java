package com.picpay.bankapi.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.picpay.bankapi.services.AccountService;
import com.picpay.bankapi.controllers.DTOs.NewUserDTO;
import com.picpay.bankapi.controllers.DTOs.AccountIdDTO;

@Controller
@AllArgsConstructor
@RequestMapping("/api/accounts/v1")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<AccountIdDTO> signUp(@RequestBody NewUserDTO params) {
        var account = accountService.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountIdDTO(account.getId()));
    }
}
