package com.picpay.bankapi.controller;

import com.picpay.bankapi.web.dto.AccountDTO;
import com.picpay.bankapi.web.dto.AccountIdDTO;
import com.picpay.bankapi.web.dto.NewAccountDTO;
import com.picpay.bankapi.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/accounts/v1")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<AccountIdDTO> signUp(@Valid @RequestBody NewAccountDTO newAccountDTO) {
        var account = accountService.createAccount(newAccountDTO);
        var accountId = new AccountIdDTO(account.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts() {
        var accounts = accountService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }
}
