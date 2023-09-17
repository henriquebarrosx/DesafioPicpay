package com.picpay.bankapi.entities;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

import java.math.BigDecimal;
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
    @JoinColumn(name = "payer_id", referencedColumnName = "id")
    private Account payer;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payee_id", referencedColumnName = "id")
    private Account payee;

    @Column
    private BigDecimal value;

    @Column(name = "was_reverted")
    private Boolean wasReversed;

    @Column(name = "is_chargeback")
    private Boolean isChargeback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
