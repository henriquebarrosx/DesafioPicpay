package com.picpay.bankapi.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.picpay.bankapi.enums.AccountTypeEnum;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountTypeEnum type;

    @Column(unique = true)
    private String cpfCnpj;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
