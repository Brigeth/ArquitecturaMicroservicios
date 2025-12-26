package com.brigeth.domain.port.output;

import com.brigeth.domain.models.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerPersistencePort {
    Flux<Customer> getAllCustomers();
    Mono<Customer> getCustomerById(String customerId);
    Mono<Customer> saveCustomer(Customer customer);
    Mono<Void> deleteCustomer(String customerId);
    Mono<Customer> updateCustomer(Customer customer);
}
