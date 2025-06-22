package com.service.transfer.entity.account;

import com.service.transfer.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "accounts",   indexes = {
        @Index(name = "idx_iban_number", columnList = "iban_number"),
        @Index(name = "idx_account_number", columnList = "account_number"),
})
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity {
    public static final Integer ACTIVE_STATUS=1;
    public static final Integer DISABLE_STATUS=2;
    public static final Integer BLOCKED_STATUS=3;
    public static final Integer SUSPENDED_STATUS=4;

    @ManyToOne(optional = false,targetEntity = AccountOwner.class)
    @JoinColumn(name = "owner_id")
    private AccountOwner owner;

    @DecimalMin(value = "0.00", message = "Balance must be zero or positive")
    @Column(name = "balance",precision = 12,scale = 5,nullable = false)
    private BigDecimal balance;

    @Column(name = "currencyIsoCode",nullable = false,length = 3)
    private String currency;

    @Column(name = "iban_number",length = 34,nullable = false, unique = true)
    private String ibanNumber;

    @Column(name = "account_number",nullable = false, length = 20,unique = true)
    private String accountNumber;

}
