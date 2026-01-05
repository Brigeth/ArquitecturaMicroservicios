package com.btoapanta.account.service.application.usecase;

import com.btoapanta.account.service.domain.enums.MovementType;
import com.btoapanta.account.service.domain.exception.notfound.AccountNotFoundException;
import com.btoapanta.account.service.domain.exception.business.InvalidBalanceException;
import com.btoapanta.account.service.domain.exception.InvalidAccountStateException;
import com.btoapanta.account.service.domain.model.Account;
import com.btoapanta.account.service.domain.model.Movement;
import com.btoapanta.account.service.application.port.input.MovementInputPort;
import com.btoapanta.account.service.application.port.output.AccountPersistencePort;
import com.btoapanta.account.service.application.port.output.MovementPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementUseCase implements MovementInputPort {

    private final AccountPersistencePort accountPersistencePort;
    private final MovementPersistencePort movementPersistencePort;

    @Override
    public Mono<Movement> createMovement(Movement movement) {
        log.info("Creating movement for account: {} - Type: {} - Amount: {}",
                movement.getAccountNumber(), movement.getMovementType(), movement.getAmount());

        return validateMovement(movement)
                .then(accountPersistencePort.getAccountByNumber(movement.getAccountNumber()))
                .switchIfEmpty(Mono.error(new AccountNotFoundException(movement.getAccountNumber())))
                .flatMap(account -> processMovement(account, movement))
                .doOnSuccess(created -> log.info("Movement created successfully with ID: {}", created.getId()))
                .doOnError(error -> log.error("Error creating movement: {}", error.getMessage()));
    }

    @Override
    public Flux<Movement> getAllMovements(Long accountNumber, MovementType movementType) {
        log.info("Fetching movements - accountNumber: {}, movementType: {}", accountNumber, movementType);

        if (accountNumber == null) {
            log.warn("AccountNumber is required to fetch movements");
            return Flux.empty();
        }

        return accountPersistencePort.getAccountByNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)))
                .flatMapMany(account -> {
                    log.info("Found account ID: {} for accountNumber: {}", account.getId(), accountNumber);

                    // Obtener movimientos por el ID de la cuenta
                    return movementPersistencePort.getMovementsByAccountId(account.getId());
                })
                .filter(movement -> movementType == null || movement.getMovementType().equals(movementType))
                .doOnComplete(() -> log.info("Movements fetched successfully"))
                .doOnError(error -> log.error("Error fetching movements: {}", error.getMessage()));
    }

    private Mono<Void> validateMovement(Movement movement) {
        return Mono.defer(() -> {
            if (movement.getAmount() == null || movement.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return Mono.error(new InvalidAccountStateException("Movement amount must be greater than zero"));
            }

            if (movement.getMovementType() == null) {
                return Mono.error(new InvalidAccountStateException("Movement type is required"));
            }

            if (movement.getAccountNumber() == null) {
                return Mono.error(new InvalidAccountStateException("Account number is required"));
            }

            return Mono.empty();
        });
    }

    private Mono<Movement> processMovement(Account account, Movement movementRequest) {
        return Mono.fromCallable(() -> {
                    return switch (movementRequest.getMovementType()) {
                        case DEBIT -> account.debit(movementRequest.getAmount());
                        case CREDIT -> account.credit(movementRequest.getAmount());
                    };
                })
                .flatMap(movement -> saveAccountAndMovement(account, movement));
    }

    private Mono<Movement> saveAccountAndMovement(Account account, Movement movement) {
        return accountPersistencePort.updateAccount(account)
                .thenReturn(movement)
                .doOnSuccess(saved -> log.info("Balance updated to {} for account {}",
                        account.getBalance(), account.getAccountNumber()));
    }
}
