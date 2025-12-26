package com.btoapanta.account.service.infrastructure.adapter.input.rest.controller;

import com.btoapanta.account.service.application.port.input.AccountInputPort;
import com.btoapanta.account.service.infrastructure.adapter.input.rest.mapper.AccountDtoMapper;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.api.AccountsApi;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountCreateRequest;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountResponse;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountType;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController implements AccountsApi {

    private final AccountInputPort accountInputPort;
    private final AccountDtoMapper accountDtoMapper;

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            Mono<AccountCreateRequest> accountCreateRequest,
            ServerWebExchange exchange) {

        log.info("REST request to create account");

        return accountCreateRequest
                .map(accountDtoMapper::toDomain)
                .flatMap(accountInputPort::createAccount)
                .map(accountDtoMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(response -> log.info("Account created with number: {}",
                        response.getBody().getAccountNumber()))
                .doOnError(error -> log.error("Error creating account: {}", error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(
            Long accountNumber,
            ServerWebExchange exchange) {

        log.info("REST request to delete account: {}", accountNumber);

        return accountInputPort.deleteAccount(accountNumber)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(v -> log.info("Account {} deleted successfully", accountNumber))
                .doOnError(error -> log.error("Error deleting account {}: {}",
                        accountNumber, error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountByNumber(
            Long accountNumber,
            ServerWebExchange exchange) {

        log.info("REST request to get account by number: {}", accountNumber);

        return accountInputPort.getAccountByNumber(accountNumber)
                .map(accountDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Account {} retrieved successfully", accountNumber))
                .doOnError(error -> log.error("Error retrieving account {}: {}",
                        accountNumber, error.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAccounts(
            UUID customerId,
            AccountType accountType,
            ServerWebExchange exchange) {

        log.info("REST request to get accounts - customerId: {}, accountType: {}",
                customerId, accountType);

        // Convert API AccountType to Domain AccountType
        com.btoapanta.account.service.domain.enums.AccountType domainAccountType =
                accountType != null ? accountDtoMapper.apiToDomainAccountType(accountType) : null;

        Flux<AccountResponse> accountsFlux = accountInputPort
                .getAllAccounts(customerId, domainAccountType)
                .map(accountDtoMapper::toResponse)
                .doOnComplete(() -> log.info("Accounts list retrieved successfully"))
                .doOnError(error -> log.error("Error retrieving accounts: {}", error.getMessage()));

        return Mono.just(ResponseEntity.ok(accountsFlux));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            Long accountNumber,
            Mono<AccountUpdateRequest> accountUpdateRequest,
            ServerWebExchange exchange) {

        log.info("REST request to update account: {}", accountNumber);

        return accountInputPort.getAccountByNumber(accountNumber)
                .zipWith(accountUpdateRequest)
                .map(tuple -> {
                    var account = tuple.getT1();
                    var updateRequest = tuple.getT2();

                    // Update only non-null fields from request
                    if (updateRequest.getAccountType() != null) {
                        account.setAccountType(accountDtoMapper.apiToDomainAccountType(updateRequest.getAccountType()));
                    }
                    if (updateRequest.getState() != null) {
                        account.setState(updateRequest.getState());
                    }

                    return account;
                })
                .flatMap(accountInputPort::updateAccount)
                .map(accountDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Account {} updated successfully", accountNumber))
                .doOnError(error -> log.error("Error updating account {}: {}",
                        accountNumber, error.getMessage()));
    }
}
