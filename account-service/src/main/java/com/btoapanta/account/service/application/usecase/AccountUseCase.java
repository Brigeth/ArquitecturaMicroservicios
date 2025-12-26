package com.btoapanta.account.service.application.usecase;

import com.btoapanta.account.service.domain.enums.AccountType;
import com.btoapanta.account.service.domain.exception.notfound.AccountNotFoundException;
import com.btoapanta.account.service.domain.exception.DuplicateAccountException;
import com.btoapanta.account.service.domain.exception.InvalidAccountStateException;
import com.btoapanta.account.service.domain.model.Account;
import com.btoapanta.account.service.application.port.input.AccountInputPort;
import com.btoapanta.account.service.application.port.output.AccountPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountUseCase implements AccountInputPort {

    private final AccountPersistencePort accountPersistencePort;

    @Override
    public Mono<Account> createAccount(Account account) {
        log.info("Creating account for customer: {}", account.getCustomerId());

        return validateAccountCreation(account)
                .then(accountPersistencePort.saveAccount(account))
                .doOnSuccess(created -> log.info("Account created successfully with number: {}", created.getAccountNumber()))
                .doOnError(error -> log.error("Error creating account: {}", error.getMessage()));
    }

    @Override
    public Mono<Account> getAccountByNumber(Long accountNumber) {
        log.info("Fetching account by number: {}", accountNumber);

        return accountPersistencePort.getAccountByNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)))
                .doOnSuccess(account -> log.info("Account found: {}", accountNumber))
                .doOnError(error -> log.error("Error fetching account {}: {}", accountNumber, error.getMessage()));
    }

    @Override
    public Flux<Account> getAllAccounts(UUID customerId, AccountType accountType) {
        log.info("Fetching accounts with filters - customerId: {}, accountType: {}", customerId, accountType);

        return accountPersistencePort.getAllAccounts()
                .filter(account -> customerId == null || account.getCustomerId().equals(customerId))
                .filter(account -> accountType == null || account.getAccountType().equals(accountType))
                .doOnComplete(() -> log.info("Accounts fetched successfully"))
                .doOnError(error -> log.error("Error fetching accounts: {}", error.getMessage()));
    }

    @Override
    public Mono<Account> updateAccount(Account account) {
        log.info("Updating account: {}", account.getAccountNumber());

        return accountPersistencePort.updateAccount(account)
                .doOnSuccess(updated -> log.info("Account {} updated successfully", account.getAccountNumber()))
                .doOnError(error -> log.error("Error updating account {}: {}",
                        account.getAccountNumber(), error.getMessage()));
    }

    @Override
    public Mono<Void> deleteAccount(Long accountNumber) {
        log.info("Deleting account: {}", accountNumber);

        return accountPersistencePort.getAccountByNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)))
                .flatMap(account -> {
                    account.setState(false);
                    return accountPersistencePort.updateAccount(account);
                })
                .then()
                .doOnSuccess(v -> log.info("Account {} deleted successfully", accountNumber))
                .doOnError(error -> log.error("Error deleting account {}: {}", accountNumber, error.getMessage()));
    }

    private Mono<Void> validateAccountCreation(Account account) {
        return Mono.defer(() -> {
            if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                return Mono.error(new InvalidAccountStateException("Initial balance cannot be negative"));
            }

            if (account.getBalance() == null) {
                account.setBalance(BigDecimal.ZERO);
            }

            if (account.getState() == null) {
                account.setState(true);
            }

            if (account.getAccountNumber() != null) {
                return accountPersistencePort.getAccountByNumber(account.getAccountNumber())
                        .flatMap(existing -> Mono.<Void>error(new DuplicateAccountException(account.getAccountNumber())))
                        .then();
            }
            return Mono.empty();
        });
    }
}
