package com.brigeth.infraestructure.adapter.input.rest;

import com.brigeth.application.service.CustomerService;
import com.brigeth.customer.infrastructure.adapter.input.rest.CustomersApi;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CreateCustomerRequest;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CustomerResponse;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.UpdateCustomerRequest;
import com.brigeth.infraestructure.adapter.input.rest.mapper.CustomerRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerRestControllerAdapter implements CustomersApi {

    private final CustomerService customerService;
    private final CustomerRestMapper customerRestMapper;

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomers(
            Mono<CreateCustomerRequest> createCustomerRequest, 
            ServerWebExchange exchange) {
        log.info("[REST] Request to create new customer");
        
        return createCustomerRequest
                .map(customerRestMapper::toDomain)
                .flatMap(customerService::createCustomer)
                .map(customerRestMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .doOnSuccess(r -> log.info("[REST] Customer created successfully: {}", r.getBody().getCustomerId()))
                .doOnError(e -> log.error("[REST] Error creating customer: {}", e.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(
            UUID customerId, 
            ServerWebExchange exchange) {
        log.info("[REST] Request to delete customer: {}", customerId);
        
        return customerService.deleteCustomer(customerId.toString())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .doOnSuccess(v -> log.info("[REST] Customer deleted successfully: {}", customerId))
                .doOnError(e -> log.error("[REST] Error deleting customer: {}", e.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAllCustomer(ServerWebExchange exchange) {
        log.info("[REST] Request to get all customers");
        
        Flux<CustomerResponse> customerResponse = customerService.getCustomers()
                .map(customerRestMapper::toResponse)
                .doOnComplete(() -> log.info("[REST] Customer list retrieved successfully"));
        
        return Mono.just(ResponseEntity.ok(customerResponse));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerById(
            UUID customerId, 
            ServerWebExchange exchange) {
        log.info("[REST] Request to find customer by ID: {}", customerId);
        
        return customerService.getOnlyCustomerById(customerId.toString())
                .map(customerRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(r -> log.info("[REST] Customer found: {}", customerId))
                .doOnError(e -> log.error("[REST] Error searching for customer: {}", e.getMessage()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(
            UUID customerId, 
            Mono<UpdateCustomerRequest> updateCustomerRequest, 
            ServerWebExchange exchange) {
        log.info("[REST] Request to update customer: {}", customerId);
        
        return updateCustomerRequest
                .map(customerRestMapper::toUpdateDomain)
                .flatMap(customer -> {
                    customer.setPersonId(customerId);
                    return customerService.updateCustomer(customer);
                })
                .map(customerRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .doOnSuccess(r -> log.info("[REST] Customer updated successfully: {}", customerId))
                .doOnError(e -> log.error("[REST] Error updating customer: {}", e.getMessage()));
    }
}
