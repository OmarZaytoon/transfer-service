package com.service.transfer.repository;

import com.service.transfer.entity.account.Account;
import com.service.transfer.entity.account.AccountOwner;
import com.service.transfer.entity.enums.IdentificationType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountOwnerRepository accountOwnerRepository;


    private AccountOwner createOwner() {
        AccountOwner owner = new AccountOwner();
        owner.setFirstName("Omar");
        owner.setMiddleName("A");
        owner.setLastName("Zaytoon");
        owner.setPhoneNumber("+962798123456");
        owner.setFullAddress("Amman, Jordan");
        owner.setNationality("Jordanian");
        owner.setIdentificationId("123456789");
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
        return account;
    }

    @Test
    void validSave(){
        Account account=createAccount(createOwner(),"GB82WEST12345698765432","131000302");
        Account saved = accountRepository.save(account);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void accountWithoutOwnerTest(){
        Account account=createAccount(null,"JO94CBJO0010000000000131000302","131000302");
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class)
                .hasMessageContaining("owner");

    }

    @Test
    void accountWithInvalidBalanceTest(){
        Account account=createAccount(createOwner(),"JO94CBJO0010000000000131000302","131000302");
        account.setBalance(new BigDecimal("-0.01"));
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class)
                .hasMessageContaining("balance");
    }
    @Test
    void accountWithNullBalanceTest(){
        Account account=createAccount(createOwner(),"JO94CBJO0010000000000131000302","131000302");
        account.setBalance(null);
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class)
                .hasMessageContaining("balance");
    }
    @Test
    void accountWithNullCurrencyTest(){
        Account account=createAccount(createOwner(),"JO94CBJO0010000000000131000302","131000302");
        account.setCurrency(null);
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class)
                .hasMessageContaining("currency");
    }
    @Test
    void accountWithInvalidCurrencyTest(){
        Account account=createAccount(createOwner(),"JO94CBJO0010000000000131000302","131000302");
        account.setCurrency("TESTS");
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class)
                .hasMessageContaining("currency");
    }
    @Test
    void accountWithNullIban(){
        Account account=createAccount(createOwner(),null,"131000302");
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class,ConstraintViolationException.class)
                .hasMessageContaining("ibanNumber");
    }
    @Test
    void accountWithNullAccountNumber(){
        Account account=createAccount(createOwner(),"JO94CBJO0010000000000131000302",null);
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class,ConstraintViolationException.class)
                .hasMessageContaining("account_number");
    }

    @Test
    void accountWithInvalidIban(){
        Account account=createAccount(createOwner(),"TESTIBAN2132IVALID231","131000302");
        assertThatThrownBy(() -> accountRepository.save(account))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class,ConstraintViolationException.class)
                .hasMessageContaining("ibanNumber");
    }
}
