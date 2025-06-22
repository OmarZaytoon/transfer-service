package com.service.transfer;

import com.service.transfer.dto.ResponseWrapper;
import com.service.transfer.dto.requests.TransferByAccountNumberRequest;
import com.service.transfer.dto.responses.TransferResponse;
import com.service.transfer.entity.account.Account;
import com.service.transfer.entity.account.AccountOwner;
import com.service.transfer.entity.enums.IdentificationType;
import com.service.transfer.entity.transactions.TransactionInfo;
import com.service.transfer.repository.AccountOwnerRepository;
import com.service.transfer.repository.AccountRepository;
import com.service.transfer.repository.TransactionInfoRepository;
import com.service.transfer.service.FundTransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FundTransferIntegrationTest {

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;


    private static AccountOwner owner;
    private static Account fromAccount;
    private static Account toAccount;
    private static List<String> transactionsIdz=new ArrayList<>();
    @BeforeEach
    public void setup() {
        owner = new AccountOwner();
        owner.setFirstName("John");
        owner.setFullAddress("123 Street");
        owner.setNationality("US");
        owner.setIdentificationId("ID123");
        owner.setIdentificationType(IdentificationType.PASSPORT_NUMBER);
        accountOwnerRepository.save(owner);

        fromAccount = new Account();
        fromAccount.setAccountNumber("98765432");
        fromAccount.setIbanNumber("GB82WEST12345698765432");
        fromAccount.setBalance(new BigDecimal("1000"));
        fromAccount.setCurrency("USD");
        fromAccount.setStatus(Account.ACTIVE_STATUS);
        fromAccount.setOwner(owner);
        accountRepository.save(fromAccount);

        toAccount = new Account();
        toAccount.setAccountNumber("32013000");
        toAccount.setIbanNumber("DE89370400440532013000");
        toAccount.setBalance(new BigDecimal("500"));
        toAccount.setCurrency("USD");
        toAccount.setStatus(Account.ACTIVE_STATUS);
        toAccount.setOwner(owner);
        accountRepository.save(toAccount);
    }

    @Test
    public void concurrentTransfersShouldOnlyAllowSingleTransactionDueToLocking() throws InterruptedException {
        int concurrencyLevel = 10;
        BigDecimal transferAmount = new BigDecimal("1000");

        ExecutorService executorService = Executors.newFixedThreadPool(concurrencyLevel);
        CountDownLatch latch = new CountDownLatch(concurrencyLevel);
        List<Future<ResponseWrapper<TransferResponse>>> futures = Collections.synchronizedList(new CopyOnWriteArrayList<>());

        IntStream.range(0, concurrencyLevel).forEach(i -> {
            executorService.submit(() -> {
                try {
                    TransferByAccountNumberRequest request = new TransferByAccountNumberRequest();
                    request.setFromAccountNumber("98765432");
                    request.setToAccountNumber("32013000");
                    request.setAmount(transferAmount);
                    ResponseWrapper<TransferResponse> response = fundTransferService.transferFundsByAccountNumber(request);
                    if(Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getTransactionId())) {
                        transactionsIdz.add(response.getBody().getTransactionId());
                    }
                    futures.add(CompletableFuture.completedFuture(response));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        });

        latch.await();
        executorService.shutdown();

        Account updatedFromAccount = accountRepository.findById(fromAccount.getId()).orElseThrow();
        assertThat(updatedFromAccount.getBalance()).isEqualByComparingTo(new BigDecimal("0"));
        assertThat(1).isEqualTo(transactionsIdz.size());
        TransactionInfo tx = transactionInfoRepository.findByTransactionId(transactionsIdz.get(0)).orElseThrow();
        assertThat(tx.getDeductedAmount()).isEqualByComparingTo(new BigDecimal("1000"));
    }
    @AfterEach
    public void cleanup() {
        transactionsIdz.forEach(t->{
            transactionInfoRepository.findByTransactionId(t).ifPresent(ti->transactionInfoRepository.deleteById(ti.getId()));
        });
        accountRepository.deleteById(fromAccount.getId());
        accountRepository.deleteById(toAccount.getId());
        accountOwnerRepository.deleteById(owner.getId());
    }
}
