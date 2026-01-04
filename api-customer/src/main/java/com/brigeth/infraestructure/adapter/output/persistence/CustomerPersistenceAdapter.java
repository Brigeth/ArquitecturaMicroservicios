package com.brigeth.infraestructure.adapter.output.persistence;

import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.models.Customer;
import com.brigeth.domain.port.output.CustomerPersistencePort;
import com.brigeth.infraestructure.adapter.output.persistence.entity.CustomerEntity;
import com.brigeth.infraestructure.adapter.output.persistence.mapper.CustomerPersistenceMapper;
import com.brigeth.infraestructure.adapter.output.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerPersistencePort {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;


    @Override
    public Flux<Customer> getAllCustomers() {
        return Mono.fromCallable(customerJpaRepository::findAll)
                .flatMapMany(Flux::fromIterable)
                .map(customerPersistenceMapper::toDomain)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> getCustomerById(String customerId) {
        log.debug("Querying database for client: {}", customerId);
        return Mono.fromCallable(() -> {
            CustomerEntity customerEntity = customerJpaRepository.findById(customerId)
                    .orElseThrow(() -> {
                        log.warn("Client not found in database: {}", customerId);
                        return new CustomerNotFoundException(customerId);
                    });
            return customerPersistenceMapper.toDomain(customerEntity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> saveCustomer(Customer customer) {
        log.debug("Saving to database: {}", customer.getIdentification());
        return Mono.fromCallable(() -> {
            CustomerEntity customerEntity = customerPersistenceMapper.toEntity(customer);
            CustomerEntity savedEntity = customerJpaRepository.save(customerEntity);
            return customerPersistenceMapper.toDomain(savedEntity);
        })
        .doOnSuccess(c -> log.debug("Client saved in database: {}", c.getPersonId()))
        .doOnError(e -> log.error("Database error while saving: {}", e.getMessage()))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        return Mono.fromCallable(() -> {
                    customerJpaRepository.deleteById(customerId);
                    return Void.TYPE;
                }).then().subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> updateCustomer(Customer customer) {
        return Mono.fromCallable(() -> {
            CustomerEntity existingEntity = customerJpaRepository.findById(customer.getPersonId().toString())
                    .orElseThrow(() -> new CustomerNotFoundException(customer.getPersonId().toString()));
                    existingEntity.setName(customer.getName());
                    existingEntity.setGender(customer.getGender().toString());
                    existingEntity.setIdentification(customer.getIdentification());
                    existingEntity.setAddress(customer.getAddress());
                    existingEntity.setPhone(customer.getPhone());
                    existingEntity.setPassword(customer.getPassword());
                    existingEntity.setState(customer.getState());
                    CustomerEntity updatedEntity = customerJpaRepository.save(existingEntity);
                    return customerPersistenceMapper.toDomain(updatedEntity);
                }
        ).subscribeOn(Schedulers.boundedElastic()
        );
    }
}
