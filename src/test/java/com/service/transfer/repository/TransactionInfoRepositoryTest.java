package com.service.transfer.repository;

import com.service.transfer.entity.account.Account;
import com.service.transfer.entity.account.AccountOwner;
import com.service.transfer.entity.enums.IdentificationType;
import com.service.transfer.entity.transactions.TransactionInfo;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionInfoRepositoryTest {

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;

    private AccountOwner createOwner() {
        AccountOwner owner = new AccountOwner();
        owner.setFirstName("Test");
        owner.setFullAddress("Some Address");
        owner.setNationality("Testland");
        owner.setIdentificationId("231123456");
        owner.setIdentificationType(IdentificationType.NATIONAL_NUMBER);
        accountOwnerRepository.save(owner);
        return owner;
    }

    private Account createAccount(AccountOwner owner, String iban, String accountNumber) {
        Account account = new Account();
        account.setOwner(owner);
        account.setBalance(new BigDecimal("1000.00"));
        account.setCurrency("USD");
        account.setIbanNumber(iban);
        account.setAccountNumber(accountNumber);
        account.setStatus(Account.ACTIVE_STATUS);
        return accountRepository.save(account);
    }
    private TransactionInfo createValidTransaction(Account from, Account to) {
        TransactionInfo tx = new TransactionInfo();
        String transactionId = System.currentTimeMillis()+"-"+UUID.randomUUID().toString();
        tx.setTransactionId(transactionId);
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setExchangeRate(BigDecimal.valueOf(1.1));
        tx.setDeductedAmount(BigDecimal.valueOf(100));
        tx.setReceivedAmount(BigDecimal.valueOf(110));
        tx.setDeductedCurrency("USD");
        tx.setReceivedCurrency("EUR");
        tx.setActionBy("testUser");
        return tx;
    }

    @Test
    void validTransactionInfo() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "GB82WEST12345698765432", "ACC001");
        Account to = createAccount(owner, "DE89370400440532013000", "ACC002");

        TransactionInfo tx = createValidTransaction(from, to);

        TransactionInfo saved = transactionInfoRepository.save(tx);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFromAccount()).isEqualTo(from);
        assertThat(saved.getToAccount()).isEqualTo(to);
    }

    @Test
    void transactionInfoWithNullTransactionId() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC003");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC004");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setTransactionId(null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("transaction_id");
    }

    @Test
    void transactionInfoWithNullFromAccount() {
        AccountOwner owner = createOwner();
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC005");

        TransactionInfo tx = createValidTransaction(null, to);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("from_account");
    }

    @Test
    void transactionInfoWithNullToAccount() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "GB82WEST12345698765432", "ACC006");

        TransactionInfo tx = createValidTransaction(from, null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("to_account");
    }

    @Test
    void transactionInfoWithNullExchangeRate() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "GB82WEST12345698765432", "ACC007");
        Account to = createAccount(owner, "DE89370400440532013000", "ACC008");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setExchangeRate(null);
        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("exchange_rate");
    }

    @Test
    void transactionInfoWithNullDeductedAmount() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC009");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC010");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setDeductedAmount(null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("deducted_amount");
    }
    @Test
    void transactionInfoWithInvalidDeductedAmount() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC009");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC010");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setDeductedAmount(new BigDecimal("-0.01"));

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class,ConstraintViolationException.class)
                .hasMessageContaining("deductedAmount");
    }

    @Test
    void transactionInfoWithNullReceivedAmount() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC009");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC010");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setReceivedAmount(null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("received_amount");
    }
    @Test
    void transactionInfoWithInvalidReceivedAmount() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC009");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC010");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setReceivedAmount(new BigDecimal("-0.01"));

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class, ConstraintViolationException.class)
                .hasMessageContaining("receivedAmount");
    }



    @Test
    void transactionInfoWithNullDeductedCurrency() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "GB82WEST12345698765432", "ACC013");
        Account to = createAccount(owner, "DE89370400440532013000", "ACC014");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setDeductedCurrency(null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("deducted_currency");
    }

    @Test
    void transactionInfoWithNullReceivedCurrency() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC015");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC016");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setReceivedCurrency(null);

        assertThatThrownBy(() -> transactionInfoRepository.saveAndFlush(tx))
                .isInstanceOfAny(ConstraintViolationException.class,
                        DataIntegrityViolationException.class)
                .hasMessageContaining("received_currency");
    }

    @Test
    void transactionInfoWithNullActionBy() {
        AccountOwner owner = createOwner();
        Account from = createAccount(owner, "DE89370400440532013000", "ACC017");
        Account to = createAccount(owner, "GB82WEST12345698765432", "ACC018");

        TransactionInfo tx = createValidTransaction(from, to);
        tx.setActionBy(null);

        TransactionInfo saved = transactionInfoRepository.save(tx);
        assertThat(saved.getActionBy()).isNull();
    }
}
