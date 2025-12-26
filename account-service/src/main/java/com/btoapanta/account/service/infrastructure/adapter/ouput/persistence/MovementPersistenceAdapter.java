package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence;

import com.btoapanta.account.service.domain.model.Movement;
import com.btoapanta.account.service.application.port.output.MovementPersistencePort;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.mapper.MovementMapper;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.repository.AccountJpaRepository;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.repository.MovementJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MovementPersistenceAdapter implements MovementPersistencePort {

    private final MovementJpaRepository movementJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final MovementMapper movementMapper;

    @Override
    public Mono<Movement> saveMovement(Movement movement) {
        return Mono.fromCallable(() -> {
                    var entity = movementMapper.toEntity(movement);

                    var accountEntity = accountJpaRepository.findByAccountNumber(movement.getAccountNumber())
                            .orElseThrow(() -> new RuntimeException("Account not found: " + movement.getAccountNumber()));

                    entity.setAccount(accountEntity);

                    return movementJpaRepository.save(entity);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> getMovementsByAccountId(UUID accountId) {
        log.info("Fetching movements for account ID: {}", accountId);

        return Mono.fromCallable(() -> movementJpaRepository.findByAccountId(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(movementMapper::toDomain)
                .doOnComplete(() -> log.info("Movements fetched successfully"));
    }
}
