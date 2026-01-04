package com.brigeth.application.service.impl;

import com.brigeth.application.service.ValidationService;
import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.exception.DuplicateIdentificationException;
import com.brigeth.domain.exception.ValidationException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl - Unit Tests")
class CustomerServiceImplTest {

    @Mock
    private CustomerPersistencePort customerPersistencePort;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;

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
    }

    @Test
    @DisplayName("You must successfully create a customer")
    void shouldCreateCustomerSuccessfully() {

        when(validationService.validateUniqueIdentification(anyString(), any()))
                .thenReturn(Mono.empty());
        when(customerPersistencePort.saveCustomer(any(Customer.class)))
                .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.createCustomer(testCustomer))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(validationService, times(1))
                .validateUniqueIdentification(testCustomer.getIdentification(), null);
        verify(customerPersistencePort, times(1))
                .saveCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("It should fail to create a client with invalid data")
    void shouldFailWhenCreatingCustomerWithInvalidData() {

        Customer invalidCustomer = Customer.builder()
                .name("J")  // Name too short
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        assertThrows(ValidationException.class, () -> {
            customerService.createCustomer(invalidCustomer);
        });

        verify(validationService, never()).validateUniqueIdentification(anyString(), any());
        verify(customerPersistencePort, never()).saveCustomer(any(Customer.class));
    }


    @Test
    @DisplayName("You must successfully acquire all customers.")
    void shouldGetAllCustomersSuccessfully() {
        Customer customer2 = Customer.builder()
                .personId(UUID.randomUUID())
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Avenida Central 456")
                .phone("0912345678")
                .password("Password456")
                .state(true)
                .build();

        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.just(testCustomer, customer2));

        StepVerifier.create(customerService.getCustomers())
                .expectNext(testCustomer)
                .expectNext(customer2)
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("You should get an empty list when there are no customers.")
    void shouldGetEmptyListWhenNoCustomers() {
        when(customerPersistencePort.getAllCustomers())
                .thenReturn(Flux.empty());

        StepVerifier.create(customerService.getCustomers())
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getAllCustomers();
    }

    @Test
        @DisplayName("You must successfully acquire a client by ID")
    void shouldGetCustomerByIdSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        when(customerPersistencePort.getCustomerById(customerId))
                .thenReturn(Mono.just(testCustomer));

        StepVerifier.create(customerService.getOnlyCustomerById(customerId))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerPersistencePort, times(1)).getCustomerById(customerId);
    }

    @Test
    @DisplayName("It should fail to obtain a client with a non-existent ID.")
    void shouldFailWhenGettingCustomerWithNonExistentId() {
        String customerId = UUID.randomUUID().toString();
        when(customerPersistencePort.getCustomerById(customerId))
                .thenReturn(Mono.error(new CustomerNotFoundException(customerId)));

        StepVerifier.create(customerService.getOnlyCustomerById(customerId))
                .expectError(CustomerNotFoundException.class)
                .verify();

        verify(customerPersistencePort, times(1)).getCustomerById(customerId);
    }

    @Test
    @DisplayName("You must successfully delete a customer")
    void shouldDeleteCustomerSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        when(validationService.validateCustomerExists(customerId))
                .thenReturn(Mono.empty());
        when(customerPersistencePort.deleteCustomer(customerId))
                .thenReturn(Mono.empty());

        StepVerifier.create(customerService.deleteCustomer(customerId))
                .verifyComplete();

        verify(validationService, times(1)).validateCustomerExists(customerId);
        verify(customerPersistencePort, times(1)).deleteCustomer(customerId);
    }

    @Test
    @DisplayName("You must successfully update a client")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        String customerId = testCustomer.getPersonId().toString();
        Customer updatedCustomer = testCustomer.toBuilder()
                .address("Nueva Direccion 789")
                .build();

        when(validationService.validateCustomerExists(customerId))
                .thenReturn(Mono.empty());
        when(validationService.validateUniqueIdentification(anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(customerPersistencePort.updateCustomer(any(Customer.class)))
                .thenReturn(Mono.just(updatedCustomer));

        StepVerifier.create(customerService.updateCustomer(updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();

        verify(validationService, times(1)).validateCustomerExists(customerId);
        verify(validationService, times(1))
                .validateUniqueIdentification(updatedCustomer.getIdentification(), customerId);
        verify(customerPersistencePort, times(1)).updateCustomer(any(Customer.class));
    }
}
