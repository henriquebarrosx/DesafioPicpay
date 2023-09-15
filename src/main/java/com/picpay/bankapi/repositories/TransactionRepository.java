package com.picpay.bankapi.repositories;

import com.picpay.bankapi.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> { }
