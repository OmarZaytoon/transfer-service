package com.service.transfer.repository;

import com.service.transfer.entity.account.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOwnerRepository extends JpaRepository<AccountOwner,Long> {
}
