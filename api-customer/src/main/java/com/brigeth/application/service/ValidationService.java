package com.brigeth.application.service;

import reactor.core.publisher.Mono;

public interface ValidationService {
    

    Mono<Void> validateUniqueIdentification(String identification, String excludeCustomerId);
    
    Mono<Void> validateCustomerExists(String customerId);
}
