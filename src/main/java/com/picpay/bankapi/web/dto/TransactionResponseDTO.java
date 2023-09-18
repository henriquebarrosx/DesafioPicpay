package com.picpay.bankapi.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal value;
    private LocalDateTime timestamp;
}
