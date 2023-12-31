package com.picpay.bankapi.controller;

import com.picpay.bankapi.entity.Transaction;
import com.picpay.bankapi.service.TransactionService;
import com.picpay.bankapi.web.dto.NewTransactionDTO;
import com.picpay.bankapi.web.dto.TransactionDTO;
import com.picpay.bankapi.web.dto.TransactionIdDTO;
import com.picpay.bankapi.web.dto.TransactionResponseDTO;
import com.picpay.bankapi.web.mapper.TransactionDTOMapper;
import com.picpay.bankapi.web.mapper.TransactionResponseDTOMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transactions/v1")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionDTOMapper transactionDTOMapper;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> registerTransaction(@RequestBody NewTransactionDTO newTransactionDTO) {
        var transaction = transactionService
                .createTransaction(
                        newTransactionDTO.value(),
                        newTransactionDTO.payerId(),
                        newTransactionDTO.payeeId()
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTOMapper.apply(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAll() {
        var transactions = transactionService.findAll()
                .stream()
                .map(transactionDTOMapper)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    @PostMapping("/{id}/chargeback")
    public ResponseEntity<TransactionIdDTO> registerChargeback(@PathVariable Long id) {
        Transaction chargeback = transactionService.createChargeback(id);
        var transactionID = new TransactionIdDTO(chargeback.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionID);
    }
}
