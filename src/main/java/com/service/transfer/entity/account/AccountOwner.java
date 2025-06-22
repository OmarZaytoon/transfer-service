package com.service.transfer.entity.account;

import com.service.transfer.entity.enums.IdentificationType;
import com.service.transfer.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "account_owner",uniqueConstraints = {@UniqueConstraint(columnNames = {"identification_id","identification_type"})})
@EqualsAndHashCode(callSuper = true)
public class AccountOwner extends BaseEntity {

    @Column(name = "first_name",length = 100,nullable = false)
    private String firstName;
    @Column(name = "middle_name",length = 100)
    private String middleName;
    @Column(name = "last_name",length = 100)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "full_address",nullable = false)
    private String fullAddress;

    @Column(name = "nationality",length = 25,nullable = false)
    private String nationality;

    @Column(name = "identification_id",length = 25,nullable = false)
    private String identificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type",length = 20,nullable = false)
    private IdentificationType identificationType;

}
