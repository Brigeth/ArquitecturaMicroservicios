package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence;

import com.btoapanta.account.service.domain.model.Account;
import com.btoapanta.account.service.application.port.output.AccountPersistencePort;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.AccountEntity;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.mapper.AccountMapper;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountPersistencePort {

    private final AccountJpaRepository accountJpaRepository;

    private final AccountMapper accountMapper;

    @Override
    public Mono<Account> getAccountByNumber(Long accountNumber) {

        return Mono.fromCallable(() -> accountJpaRepository.findByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(Mono::justOrEmpty)
                .map(accountMapper::ToDomain);
    }

    @Override
    public Flux<Account> getAllAccounts() {

        return Mono.fromCallable(accountJpaRepository::findAll)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .map(accountMapper::ToDomain);
    }

    @Override
    public Mono<Account> saveAccount(Account account) {
        return Mono.fromCallable(() -> {
            AccountEntity entity = accountMapper.toEntity(account);
            return accountJpaRepository.save(entity);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(accountMapper::ToDomain);
    }

    @Override
    public Mono<Void> deleteAccount(UUID accountId) {
        return Mono.fromCallable(() -> accountJpaRepository.findById(accountId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional ->{
                   if(optional.isPresent()){
                       accountJpaRepository.delete(optional.get());
                       return Mono.empty();
                   }
                   return Mono.error(new RuntimeException("Account not found"));
                });

    }

    @Override
    public Mono<Account> updateAccount(Account account) {
        return Mono.fromCallable(() -> {
                    AccountEntity entity = accountMapper.toEntity(account);
                    
                    // Establish a bidirectional relationship with movements
                    if (entity.getMovementEntityList() != null) {
                        entity.getMovementEntityList().forEach(movement -> movement.setAccount(entity));
                    }
                    
                    return accountJpaRepository.save(entity);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(accountMapper::ToDomain);
    }
}
