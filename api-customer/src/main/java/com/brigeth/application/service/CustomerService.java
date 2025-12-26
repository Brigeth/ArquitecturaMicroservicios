package com.brigeth.application.service;

import com.brigeth.domain.models.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<Customer> createCustomer(Customer customer);
    Flux<Customer> getCustomers();
    Mono<Customer> getOnlyCustomerById(String customerId);
    Mono<Void> deleteCustomer(String customerId);
    Mono<Customer> updateCustomer(Customer customer);
}
