package com.service.transfer.service.impl;

import com.service.transfer.client.ExchangeRateClient;
import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.requests.TransferByIbanRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.entity.account.Account;
import com.service.transfer.entity.transactions.TransactionInfo;
import com.service.transfer.exceptions.EntityNotFoundException;
import com.service.transfer.exceptions.InsufficientBalanceException;
import com.service.transfer.exceptions.SameIdentityException;
import com.service.transfer.repository.TransactionInfoRepository;
import org.springframework.transaction.annotation.Transactional;
import com.service.transfer.repository.AccountRepository;
import com.service.transfer.service.FundTransferService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FundTransferServiceImpl implements FundTransferService {
    private final static Logger logger = LoggerFactory.getLogger(FundTransferServiceImpl.class);
    private final AccountRepository accountRepository;
    private final ExchangeRateClient exchangeRateClient;
    private final TransactionInfoRepository transactionInfoRepository;

    @Override
    @Transactional
    public ResponseWrapper<TransferResponse> transferFundsByAccountNumber(TransferByAccountNumberRequest request) {
        logger.info("start transfer fund from account with number [{}], to [{}], and amount [{}]", request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());
        assertNotEqualOrThrowException(request.getFromAccountNumber(), request.getToAccountNumber());
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber()).orElseThrow(EntityNotFoundException::new);
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber()).orElseThrow(EntityNotFoundException::new);
        logger.info("both accounts found and start transfer process by account number");
        return transfer(fromAccount, toAccount, request.getAmount());
    }

    @Override
    @Transactional
    public ResponseWrapper<TransferResponse> transferFundsByIban(TransferByIbanRequest request) {
        logger.info("start transfer fund from account with iban [{}], to [{}], and amount [{}]", request.getFromIban(), request.getToIban(), request.getAmount());
        assertNotEqualOrThrowException(request.getFromIban(), request.getToIban());
        Account fromAccount = accountRepository.findByIbanNumber(request.getFromIban()).orElseThrow(EntityNotFoundException::new);
        Account toAccount = accountRepository.findByIbanNumber(request.getToIban()).orElseThrow(EntityNotFoundException::new);
        logger.info("both accounts found and start transfer process by iban");
        return transfer(fromAccount, toAccount, request.getAmount());
    }


    private ResponseWrapper<TransferResponse> transfer(Account from, Account to, BigDecimal amount) {
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        logger.info("start retrieving exchange rate between [{}] and [{}]", from.getCurrency(), to.getCurrency());
        BigDecimal rate = exchangeRateClient.getExchangeRate(from.getCurrency(), to.getCurrency());
        logger.info("exchange rate between [{}] and [{}] is [{}]", from.getCurrency(), to.getCurrency(), rate);
        BigDecimal convertedAmount = amount.multiply(rate);
        logger.info("from account balance before transaction [{}], and to account balance before transaction is [{}], original Transaction Amount is [{}], and converted Amount is [{}]", from.getBalance(), to.getBalance(), amount, convertedAmount);
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(convertedAmount));
        accountRepository.save(from);
        accountRepository.save(to);
        TransactionInfo transactionInfo = generateTransactionRecord(from, to, amount, convertedAmount, rate);
        logger.info("transaction record generated successfully with transaction id [{}]", transactionInfo.getTransactionId());
        return ResponseWrapper.of(new TransferResponse(transactionInfo.getTransactionId()));
    }

    private TransactionInfo generateTransactionRecord(Account from, Account to, BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate) {
        String transactionId = System.currentTimeMillis() + "-" + UUID.randomUUID();
        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setFromAccount(from);
        transactionInfo.setToAccount(to);
        transactionInfo.setDeductedAmount(amount);
        transactionInfo.setReceivedAmount(convertedAmount);
        transactionInfo.setExchangeRate(rate);
        transactionInfo.setTransactionId(transactionId);
        transactionInfo.setDeductedCurrency(from.getCurrency());
        transactionInfo.setReceivedCurrency(to.getCurrency());
        return transactionInfoRepository.save(transactionInfo);
    }

    public void assertNotEqualOrThrowException(String s1, String s2) {
        if(StringUtils.equals(s1,s2)){
            throw new SameIdentityException();
        }
    }
}
