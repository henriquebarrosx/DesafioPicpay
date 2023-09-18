package com.picpay.bankapi.repository;

import com.picpay.bankapi.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(value = "SELECT * FROM account acc WHERE acc.cpf_cnpj = :cpf_cnpj OR acc.email = :email", nativeQuery = true)
    Optional<Account> findByCpfCnpjOrEmail(
            @Param("cpf_cnpj") String cpfCnpj,
            @Param("email") String email);
}
