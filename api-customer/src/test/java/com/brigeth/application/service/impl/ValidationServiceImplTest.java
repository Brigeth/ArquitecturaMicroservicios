package com.brigeth.application.service.impl;

import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.exception.DuplicateIdentificationException;
import com.brigeth.domain.models.Customer;
import com.brigeth.domain.port.output.CustomerPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidationServiceImpl - Unit Tests")
class ValidationServiceImplTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @InjectMocks
    private ValidationServiceImpl validationService;

    private Customer testCustomer;
    private Customer anotherCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .personId(UUID.randomUUID())
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        anotherCustomer = Customer.builder()
                .personId(UUID.randomUUID())
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Avenida Central 456")
                .phone("0912345678")
                .password("Password456")
                .state(true)
                .build();
    }

    @Test
    @DisplayName("You must successfully validate the unique identification when there is no duplicate.")
    void shouldValidateUniqueIdentificationSuccessfully() {
        String newIdentification = "1111111111";
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, anotherCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(newIdentification, null))
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("It should throw an exception when the ID is duplicated.")
    void shouldThrowExceptionWhenIdentificationIsDuplicated() {
        String duplicatedIdentification = "1234567890";
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, anotherCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(duplicatedIdentification, null))
                .expectError(DuplicateIdentificationException.class)
                .verify();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
        @DisplayName("It must allow duplicate identification when it belongs to the same client (update)")
    void shouldAllowDuplicateIdentificationForSameCustomer() {
        String customerId = testCustomer.getPersonId().toString();
        String identification = testCustomer.getIdentification();
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, anotherCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(identification, customerId))
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("You must throw an exception when the ID is duplicated on another client during an update")
    void shouldThrowExceptionWhenIdentificationIsDuplicatedInAnotherCustomer() {

        String customerId = testCustomer.getPersonId().toString();
        String duplicatedIdentification = anotherCustomer.getIdentification();
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, anotherCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(duplicatedIdentification, customerId))
                .verifyError(DuplicateIdentificationException.class);

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("You must validate unique identification when there are no customers.")
    void shouldValidateUniqueIdentificationWhenNoCustomersExist() {
        String newIdentification = "1234567890";
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.empty());

        StepVerifier.create(validationService.validateUniqueIdentification(newIdentification, null))
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("You must successfully validate that the client exists")
    void shouldValidateCustomerExistsSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        when(customerPersistencePort.getCustomerById(customerId))
                .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(validationService.validateCustomerExists(customerId))
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getCustomerById(customerId);
    }


    @Test
    @DisplayName("You must map the repository error to CustomerNotFoundException")
    void shouldMapRepositoryErrorToCustomerNotFoundException() {
        // Given
        String customerId = UUID.randomUUID().toString();
        when(customerPersistencePort.getCustomerById(customerId))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(validationService.validateCustomerExists(customerId))
                .expectError(CustomerNotFoundException.class)
                .verify();

        verify(customerPersistencePort, times(1)).getCustomerById(customerId);
    }

    @Test
    @DisplayName("You must validate multiple clients with unique IDs")
    void shouldValidateMultipleCustomersWithUniqueIdentifications() {
        String newIdentification1 = "1111111111";
        String newIdentification2 = "2222222222";
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, anotherCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(newIdentification1, null))
                .verifyComplete();

        StepVerifier.create(validationService.validateUniqueIdentification(newIdentification2, null))
                .verifyComplete();

        verify(customerPersistencePort, times(2)).getAllCustomers();
    }

    @Test
    @DisplayName("You must validate correctly with excludeCustomerId null")
    void shouldValidateCorrectlyWithNullExcludeCustomerId() {
        String identification = testCustomer.getIdentification();
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer));

        StepVerifier.create(validationService.validateUniqueIdentification(identification, null))
                .expectError(DuplicateIdentificationException.class)
                .verify();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }
}
