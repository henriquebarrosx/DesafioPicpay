package com.picpay.bankapi.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payer_id", referencedColumnName = "id", nullable = false)
    private Account payer;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payee_id", referencedColumnName = "id", nullable = false)
    private Account payee;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(name = "was_reverted", nullable = false)
    private Boolean wasReversed;

    @Column(name = "is_chargeback", nullable = false)
    private Boolean isChargeback;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
