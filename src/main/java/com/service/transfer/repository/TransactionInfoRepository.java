package com.service.transfer.repository;

import com.service.transfer.entity.transactions.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionInfoRepository extends JpaRepository<TransactionInfo, Long> {
    Optional<TransactionInfo> findByTransactionId(String transactionId);
}
