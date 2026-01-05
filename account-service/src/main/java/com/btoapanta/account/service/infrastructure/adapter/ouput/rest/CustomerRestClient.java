package com.btoapanta.account.service.infrastructure.adapter.ouput.rest;

import com.btoapanta.account.service.infrastructure.adapter.ouput.rest.dto.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CustomerRestClient {
    
    private final WebClient webClient;
    
    public CustomerRestClient(@Value("${integration.api-customer.url}") String baseUrl) {
        log.info("Initializing CustomerRestClient with baseUrl: {}", baseUrl);
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
    
    /**
     Obbtains customer information by their ID
      @param customerId
      @return Mono with customer information
     */
    public Mono<CustomerResponse> getCustomerById(UUID customerId) {
        log.info("Calling api-customer to get customer with id: {}", customerId);
        
        return webClient
                .get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError(),
                    response -> {
                        log.error("Customer not found with id: {}", customerId);
                        return Mono.error(new CustomerNotFoundException("Customer not found with id: " + customerId));
                    }
                )
                .onStatus(
                    status -> status.is5xxServerError(),
                    response -> {
                        log.error("api-customer service error");
                        return Mono.error(new CustomerServiceUnavailableException("api-customer service is unavailable"));
                    }
                )
                .bodyToMono(CustomerResponse.class)
                .doOnSuccess(customer -> log.info("Customer found: {}", customer.getName()))
                .doOnError(error -> log.error("Error fetching customer: {}", error.getMessage()));
    }
    
    /**
     * Check if a customer exists
     * @param customerId
     * @return Mono<Boolean> true if it exists, false if it does not
     */
    public Mono<Boolean> customerExists(UUID customerId) {
        return getCustomerById(customerId)
                .map(customer -> true)
                .onErrorReturn(CustomerNotFoundException.class, false);
    }
}
