package com.btoapanta.account.service.application.port.output;

import com.btoapanta.account.service.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MovementPersistencePort {

    Mono<Movement> saveMovement(Movement movement);

    Flux<Movement> getMovementsByAccountId(UUID accountId);
}
