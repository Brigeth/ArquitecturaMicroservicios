package com.btoapanta.account.service.application.port.input;

import com.btoapanta.account.service.domain.enums.AccountType;
import com.btoapanta.account.service.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountInputPort {

    Mono<Account> createAccount(Account account);

    Mono<Account> getAccountByNumber(Long accountNumber);

    Flux<Account> getAllAccounts(UUID customerId, AccountType accountType);

    Mono<Account> updateAccount(Account account);

    Mono<Void> deleteAccount(Long accountNumber);
}
