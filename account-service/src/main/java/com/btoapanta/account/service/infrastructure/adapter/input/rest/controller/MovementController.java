package com.btoapanta.account.service.infrastructure.adapter.input.rest.controller;

import com.btoapanta.account.service.application.port.input.MovementInputPort;
import com.btoapanta.account.service.infrastructure.adapter.input.rest.mapper.MovementDtoMapper;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.api.MovementsApi;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementCreateRequest;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementResponse;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MovementController implements MovementsApi {

    private final MovementInputPort movementInputPort;
    private final MovementDtoMapper movementDtoMapper;

    @Override
    public Mono<ResponseEntity<MovementResponse>> createMovement(
            Mono<MovementCreateRequest> movementCreateRequest,
            ServerWebExchange exchange) {

        log.info("REST request to create movement");

        return movementCreateRequest
                .map(movementDtoMapper::toDomain)
                .flatMap(movementInputPort::createMovement)
                .map(movementDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(response -> log.info("Movement created with ID: {} for account: {}",
                        response.getBody().getMovementId(),
                        response.getBody().getAccountNumber()))
                .doOnError(error -> log.error("Error creating movement: {}", error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementResponse>>> getMovements(
            Long accountNumber,
            MovementType movementType,
            ServerWebExchange exchange) {

        log.info("REST request to get movements - accountNumber: {}, movementType: {}", accountNumber, movementType);

        com.btoapanta.account.service.domain.enums.MovementType domainMovementType =
                movementType != null ? movementDtoMapper.apiToDomainMovementType(movementType) : null;

        return movementInputPort.getAllMovements(accountNumber, domainMovementType)
                .map(movementDtoMapper::toResponse)
                .collectList()
                .doOnSuccess(list -> log.info("Retrieved {} movements for account: {}", list.size(), accountNumber))
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)));
    }
}
