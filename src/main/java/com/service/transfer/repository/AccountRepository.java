package com.service.transfer.repository;

import com.service.transfer.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByIbanNumber(String ibanNumber);
    Optional<Account> findByAccountNumber(String accountNumber);
}
