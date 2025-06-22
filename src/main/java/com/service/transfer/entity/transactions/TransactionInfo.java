package com.service.transfer.entity.transactions;

import com.service.transfer.entity.account.Account;
import com.service.transfer.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transactions_info", indexes = {
        @Index(name = "idx_transaction_id", columnList = "transaction_id")})
@EqualsAndHashCode(callSuper = true)
public class TransactionInfo extends BaseEntity {

    public static final Integer SUCCESS=1;
    public static final Integer FAILED=2;

    @Column(name = "transaction_id", nullable = false,unique = true)
    private String transactionId;

    @ManyToOne(optional = false,targetEntity = Account.class)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(optional = false,targetEntity = Account.class)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    @Column(name = "exchange_rate",nullable = false)
    private BigDecimal exchangeRate;

    @Check(constraints = "deducted_amount >= 0")
    @Column(name = "deducted_amount",precision = 12,scale = 5,nullable = false)
    @DecimalMin(value = "0.00", message = "Balance must be zero or positive")
    private BigDecimal deductedAmount;

    @Check(constraints = "received_amount >= 0")
    @Column(name = "received_amount",precision = 12,scale = 5,nullable = false)
    @DecimalMin(value = "0.00", message = "Balance must be zero or positive")
    private BigDecimal receivedAmount;

    @Column(name = "deducted_currency", nullable = false)
    private String deductedCurrency;

    @Column(name = "received_currency", nullable = false)
    private String receivedCurrency;

    @Column(name = "action_by")
    private String actionBy;

}
