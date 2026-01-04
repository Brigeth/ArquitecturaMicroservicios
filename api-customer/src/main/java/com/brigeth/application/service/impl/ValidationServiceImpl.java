package com.brigeth.application.service.impl;

import com.brigeth.application.service.ValidationService;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.exception.DuplicateIdentificationException;
import com.brigeth.domain.port.output.CustomerPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final CustomerPersistencePort customerPersistencePort;

    @Override
    public Mono<Void> validateUniqueIdentification(String identification, String excludeCustomerId) {
        log.debug("Validating unique identification: {}", identification);
        
        return customerPersistencePort.getAllCustomers()
            .filter(customer -> customer.getIdentification().equals(identification))
            .filter(customer -> excludeCustomerId == null || 
                    !customer.getPersonId().toString().equals(excludeCustomerId))
            .hasElements()
            .flatMap(exists -> {
                if (exists) {
                    log.warn("Duplicate identification: {}", identification);
                    return Mono.error(new DuplicateIdentificationException(identification));
                }
                return Mono.empty();
            });
    }

    @Override
    public Mono<Void> validateCustomerExists(String customerId) {
        log.debug("Validating customer existence: {}", customerId);
        
        return customerPersistencePort.getCustomerById(customerId)
            .then()
            .onErrorMap(e -> {
                log.warn("Customer not found: {}", customerId);
                return new CustomerNotFoundException(customerId);
            });
    }
}
