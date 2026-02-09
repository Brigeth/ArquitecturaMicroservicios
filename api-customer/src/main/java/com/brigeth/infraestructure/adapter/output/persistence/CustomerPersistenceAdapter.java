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

import java.util.UUID;

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
            CustomerEntity customerEntity = customerJpaRepository.findById(UUID.fromString(customerId))
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
                    customerJpaRepository.deleteById(UUID.fromString(customerId));
                    return Void.TYPE;
                }).then().subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Customer> updateCustomer(Customer customer) {
        return Mono.fromCallable(() -> {
            CustomerEntity existingEntity = customerJpaRepository.findById(customer.getPersonId())
                    .orElseThrow(() -> new CustomerNotFoundException(customer.getPersonId().toString()));
                    
                    // Solo actualiza los campos que vienen en la petición
                    if (customer.getName() != null) {
                        existingEntity.setName(customer.getName());
                    }
                    if (customer.getGender() != null) {
                        existingEntity.setGender(customer.getGender().toString());
                    }
                    if (customer.getIdentification() != null) {
                        existingEntity.setIdentification(customer.getIdentification());
                    }
                    if (customer.getAddress() != null) {
                        existingEntity.setAddress(customer.getAddress());
                    }
                    if (customer.getPhone() != null) {
                        existingEntity.setPhone(customer.getPhone());
                    }
                    if (customer.getPassword() != null) {
                        existingEntity.setPassword(customer.getPassword());
                    }
                    if (customer.getState() != null) {
                        existingEntity.setState(customer.getState());
                    }
                    
                    CustomerEntity updatedEntity = customerJpaRepository.save(existingEntity);
                    return customerPersistenceMapper.toDomain(updatedEntity);
                }
        ).subscribeOn(Schedulers.boundedElastic()
        );
    }
}
