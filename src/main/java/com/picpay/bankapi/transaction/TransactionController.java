package com.picpay.bankapi.transaction;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/transactions/v1")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> registerTransaction(@RequestBody TransactionDTO transactionDTO) {
        var transaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTOMapper.apply(transaction));
    }

    @GetMapping("/{id}/chargeback")
    public ResponseEntity<TransactionIdDTO> registerChargeback(@PathVariable Long id) {
        Transaction chargeback = transactionService.createChargeback(id);
        var transactionID = new TransactionIdDTO(chargeback.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(transactionID);
    }
}
