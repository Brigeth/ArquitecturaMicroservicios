package com.btoapanta.account.service.application.port.input;

import com.btoapanta.account.service.domain.enums.MovementType;
import com.btoapanta.account.service.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementInputPort {

    Mono<Movement> createMovement(Movement movement);

    Flux<Movement> getAllMovements(Long accountNumber, MovementType movementType);
}
