package com.picpay.bankapi.controllers;

import com.picpay.bankapi.services.TransactionService;
import com.picpay.bankapi.controllers.DTOs.NewTransactionDTO;
import com.picpay.bankapi.controllers.DTOs.TransactionResponseDTO;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/transactions/v1")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody NewTransactionDTO params) {
        var transaction = transactionService.createTransaction(params);

        var response = TransactionResponseDTO
                .builder()
                .id(transaction.getId())
                .value(transaction.getValue())
                .timestamp(transaction.getCreatedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/reverse")
    public ResponseEntity<Boolean> reverse(@PathVariable Long id) {
        transactionService.reverseTransaction(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
