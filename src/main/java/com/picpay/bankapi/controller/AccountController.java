package com.picpay.bankapi.controller;

import com.picpay.bankapi.web.dto.AccountDTO;
import com.picpay.bankapi.web.dto.AccountIdDTO;
import com.picpay.bankapi.web.dto.NewAccountDTO;
import com.picpay.bankapi.service.AccountService;
import com.picpay.bankapi.web.mapper.AccountDTOMapper;
import com.picpay.bankapi.web.mapper.NewAccountDTOMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/accounts/v1")
public class AccountController {
    private final AccountService accountService;
    private final AccountDTOMapper accountDTOMapper;
    private final NewAccountDTOMapper newAccountDTOMapper;

    @PostMapping("/sign-up")
    public ResponseEntity<AccountIdDTO> signUp(@Valid @RequestBody NewAccountDTO newAccountDTO) {
        var account = accountService.createAccount(newAccountDTOMapper.apply(newAccountDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountIdDTO(account.getId()));
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAccounts() {
        var accounts = accountService.findAll().stream()
                .map(accountDTOMapper)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }
}
