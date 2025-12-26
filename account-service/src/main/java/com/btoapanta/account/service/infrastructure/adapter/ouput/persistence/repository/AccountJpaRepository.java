package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.repository;

import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository <AccountEntity, UUID> {
    Optional<AccountEntity> findByAccountNumber(Long accountNumber);
}
