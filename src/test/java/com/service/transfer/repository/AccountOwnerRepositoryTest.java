package com.service.transfer.repository;

import com.service.transfer.entity.account.AccountOwner;
import com.service.transfer.entity.enums.IdentificationType;
import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountOwnerRepositoryTest {

    @Autowired
    private AccountOwnerRepository accountOwnerRepository;


    private AccountOwner createValidOwner() {
        AccountOwner owner = new AccountOwner();
        owner.setFirstName("Omar");
        owner.setMiddleName("A");
        owner.setLastName("Zaytoon");
        owner.setPhoneNumber("+962798123456");
        owner.setFullAddress("Amman, Jordan");
        owner.setNationality("Jordanian");
        owner.setIdentificationId("123456789");
        owner.setIdentificationType(IdentificationType.NATIONAL_NUMBER);
        return owner;
    }
    private String generateStringByLength(int length) {
        return "a".repeat(Math.max(0, length));
    }

    @Test
    void validSave() {
        AccountOwner owner = createValidOwner();
        AccountOwner saved = accountOwnerRepository.save(owner);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo(owner.getFirstName());
        assertThat(saved.getPhoneNumber()).isEqualTo(owner.getPhoneNumber());
    }

    @Test
    void accountOwnerWithNullFirstName() {
        AccountOwner owner = createValidOwner();
        owner.setFirstName(null);
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("firstName", "first_name");
                });
    }
    @Test
    void accountOwnerWithLongFirstName() {
        AccountOwner owner = createValidOwner();
        owner.setFirstName(generateStringByLength(101));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("firstName", "first_name");
                });
    }
    @Test
    void accountOwnerWithLongMiddleName() {
        AccountOwner owner = createValidOwner();
        owner.setMiddleName(generateStringByLength(101));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("middle_name", "middleName");
                });
    }
    @Test
    void accountOwnerWithLongLastName() {
        AccountOwner owner = createValidOwner();
        owner.setLastName(generateStringByLength(101));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("lastName", "last_name");
                });
    }

    @Test
    void accountOwnerWithNullPhoneNumber() {
        AccountOwner owner = createValidOwner();
        owner.setPhoneNumber(null);
        AccountOwner saved = accountOwnerRepository.save(owner);
        assertThat(saved.getPhoneNumber()).isNull();
    }


    @Test
    void accountOwnerWithNullFullAddress() {
        AccountOwner owner = createValidOwner();
        owner.setFullAddress(null);
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("full_address", "fullAddress");
                });
    }
    @Test
    void accountOwnerWithInvalidFullAddress() {
        AccountOwner owner = createValidOwner();
        owner.setFullAddress(generateStringByLength(256));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("full_address", "fullAddress");
                });
    }


    @Test
    void accountOwnerWithNullNationality() {
        AccountOwner owner = createValidOwner();
        owner.setNationality(null);
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .hasMessageContaining("nationality");
    }

    @Test
    void accountOwnerWithInvalidNationality() {
        AccountOwner owner = createValidOwner();
        owner.setNationality(generateStringByLength(26));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .hasMessageContaining("nationality");
    }
    @Test
    void accountOwnerWithNullIdentificationId() {
        AccountOwner owner = createValidOwner();
        owner.setIdentificationId(null);
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("identification_id", "identificationId");
                });
    }
    @Test
    void accountOwnerWithInvalidIdentificationId() {
        AccountOwner owner = createValidOwner();
        owner.setIdentificationId(generateStringByLength(26));
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("identification_id", "identificationId");
                });
    }
    @Test
    void accountOwnerWithNullIdentificationType() {
        AccountOwner owner = createValidOwner();
        owner.setIdentificationType(null);
        assertThatThrownBy(() -> accountOwnerRepository.saveAndFlush(owner))
                .isInstanceOfAny(ConstraintViolationException.class, DataIntegrityViolationException.class, PropertyValueException.class)
                .satisfies(ex -> {
                    String msg = ex.getMessage();
                    assertThat(msg).containsAnyOf("identification_type", "identificationType");
                });
    }

}
