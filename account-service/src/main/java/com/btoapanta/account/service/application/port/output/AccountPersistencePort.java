package com.btoapanta.account.service.application.port.output;

import com.btoapanta.account.service.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountPersistencePort {
    Mono<Account> getAccountByNumber(Long accountNumber);
    Flux<Account> getAllAccounts();
    Mono<Account> saveAccount(Account account);
    Mono<Void> deleteAccount(UUID accountId);
    Mono<Account> updateAccount(Account account);


}
