package com.picpay.bankapi.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/accounts/v1")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<AccountIdDTO> signUp(@RequestBody AccountDTO accountDTO) {
        var account = accountService.createAccount(accountDTO);
        var accountId = new AccountIdDTO(account.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
    }
}
