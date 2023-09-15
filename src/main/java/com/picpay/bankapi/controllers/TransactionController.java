package com.picpay.bankapi.controllers;

import com.picpay.bankapi.controllers.DTOs.TransactionIdDTO;
import com.picpay.bankapi.controllers.DTOs.NewTransactionDTO;

import com.picpay.bankapi.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/transactions/v1")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionIdDTO> create(@RequestBody NewTransactionDTO params) {
        var transaction = transactionService.create(params);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionIdDTO(transaction.getId()));
    }
}
