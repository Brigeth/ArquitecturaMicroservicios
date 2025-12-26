package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.repository;

import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovementJpaRepository extends JpaRepository<MovementEntity, UUID> {

    List<MovementEntity> findByAccountId(UUID accountId);
}
