package com.service.transfer.service;


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
import com.service.transfer.repository.AccountRepository;
import com.service.transfer.repository.TransactionInfoRepository;
import com.service.transfer.service.impl.FundTransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FundTransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @Mock
    private TransactionInfoRepository transactionInfoRepository;

    @InjectMocks
    private FundTransferService fundTransferService;


    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account();
        fromAccount.setAccountNumber("131000302");
        fromAccount.setIbanNumber("JO94CBJO0010000000000131000302");
        fromAccount.setBalance(BigDecimal.valueOf(1000));
        fromAccount.setCurrency("USD");

        toAccount = new Account();
        toAccount.setAccountNumber("1310003021");
        toAccount.setIbanNumber("JO94CBJO0010000000000131000301");
        toAccount.setBalance(BigDecimal.valueOf(500));
        toAccount.setCurrency("EUR");
    }

    @Test
    void successTransferWithAccountId() {
        TransferByAccountNumberRequest request = new TransferByAccountNumberRequest();
        request.setFromAccountNumber("1310003021");
        request.setToAccountNumber("1310003022");
        request.setAmount(new BigDecimal("100"));

        when(accountRepository.findByAccountNumber("1310003021")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("1310003022")).thenReturn(Optional.of(toAccount));
        when(exchangeRateClient.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.9));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionInfoRepository.save(any(TransactionInfo.class))).thenAnswer(invocation -> {
            TransactionInfo tx = invocation.getArgument(0);
            tx.setId(1L);
            return tx;
        });

        ResponseWrapper<TransferResponse> response = fundTransferService.transferFundsByAccountNumber(request);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTransactionId()).isNotNull();

        assertThat(fromAccount.getBalance()).isEqualByComparingTo(new BigDecimal("900"));
        assertThat(toAccount.getBalance()).isEqualByComparingTo(new BigDecimal("590"));

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionInfoRepository, times(1)).save(any(TransactionInfo.class));
    }

    @Test
    void successTransferWithIban() {
        TransferByIbanRequest request = new TransferByIbanRequest();
        request.setFromIban("JO94CBJO0010000000000131000301");
        request.setToIban("JO94CBJO0010000000000131000302");
        request.setAmount(new BigDecimal("50"));

        when(accountRepository.findByIbanNumber("JO94CBJO0010000000000131000301")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIbanNumber("JO94CBJO0010000000000131000302")).thenReturn(Optional.of(toAccount));
        when(exchangeRateClient.getExchangeRate("USD", "EUR")).thenReturn(BigDecimal.valueOf(0.85));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionInfoRepository.save(any(TransactionInfo.class))).thenAnswer(invocation -> {
            TransactionInfo tx = invocation.getArgument(0);
            tx.setId(2L);
            return tx;
        });

        ResponseWrapper<TransferResponse> response = fundTransferService.transferFundsByIban(request);

        assertThat(response).isNotNull();
        assertThat(response.getBody().getTransactionId()).isNotBlank();

        assertThat(response.getBody()).isNotNull();
        assertThat(fromAccount.getBalance()).isEqualByComparingTo(new BigDecimal("950"));
        assertThat(toAccount.getBalance()).isEqualByComparingTo(new BigDecimal("542.5"));

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionInfoRepository, times(1)).save(any(TransactionInfo.class));
    }

    @Test
    void transferFundsBySameAccountNumber() {
        TransferByAccountNumberRequest request = new TransferByAccountNumberRequest();
        request.setFromAccountNumber("1310003021");
        request.setToAccountNumber("1310003021");
        request.setAmount(new BigDecimal("100"));

        assertThatThrownBy(() -> fundTransferService.transferFundsByAccountNumber(request))
                .isInstanceOf(SameIdentityException.class);
    }

    @Test
    void transferFundsBySameIban() {
        TransferByIbanRequest request = new TransferByIbanRequest();
        request.setFromIban("JO94CBJO0010000000000131000301");
        request.setToIban("JO94CBJO0010000000000131000301");
        request.setAmount(new BigDecimal("100"));

        assertThatThrownBy(() -> fundTransferService.transferFundsByIban(request))
                .isInstanceOf(SameIdentityException.class);
    }

    @Test
    void transferByNotFoundAccountNumber() {
        TransferByAccountNumberRequest request = new TransferByAccountNumberRequest();
        request.setFromAccountNumber("1310003021");
        request.setToAccountNumber("1310003023");
        request.setAmount(new BigDecimal("100"));

        when(accountRepository.findByAccountNumber("1310003021")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fundTransferService.transferFundsByAccountNumber(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void transferByNotFoundIban() {
        TransferByIbanRequest request = new TransferByIbanRequest();
        request.setFromIban("JO94CBJO0010000000000131000301");
        request.setToIban("JO94CBJO0010000000000131000302");
        request.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findByIbanNumber("JO94CBJO0010000000000131000301")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fundTransferService.transferFundsByIban(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void transferInsufficientBalance() {
        TransferByAccountNumberRequest request = new TransferByAccountNumberRequest();
        request.setFromAccountNumber("1310003021");
        request.setToAccountNumber("1310003022");
        request.setAmount(new BigDecimal("1500"));

        when(accountRepository.findByAccountNumber("1310003021")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("1310003022")).thenReturn(Optional.of(toAccount));

        assertThatThrownBy(() -> fundTransferService.transferFundsByAccountNumber(request))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
