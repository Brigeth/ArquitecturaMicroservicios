package com.brigeth.infraestructure.adapter.input.rest;

import com.brigeth.application.service.CustomerService;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CreateCustomerRequest;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CustomerResponse;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.UpdateCustomerRequest;
import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.models.Customer;
import com.brigeth.infraestructure.adapter.input.rest.mapper.CustomerRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRestControllerAdapter - Unit Tests")
class CustomerRestControllerAdapterTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerRestMapper customerRestMapper;

    @InjectMocks
    private CustomerRestControllerAdapter customerRestControllerAdapter;

    private Customer testCustomer;
    private CustomerResponse testResponse;
    private CreateCustomerRequest createRequest;
    private UpdateCustomerRequest updateRequest;

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

        testResponse = new CustomerResponse();
        testResponse.setCustomerId(testCustomer.getPersonId());
        testResponse.setName(testCustomer.getName());

        createRequest = new CreateCustomerRequest();
        createRequest.setName("Juan Perez");
        createRequest.setIdentification("1234567890");

        updateRequest = new UpdateCustomerRequest();
        updateRequest.setName("Juan Perez Updated");
        updateRequest.setAddress("Nueva Direccion");
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        when(customerRestMapper.toDomain(any(CreateCustomerRequest.class))).thenReturn(testCustomer);
        when(customerService.createCustomer(any(Customer.class))).thenReturn(Mono.just(testCustomer));
        when(customerRestMapper.toResponse(any(Customer.class))).thenReturn(testResponse);

        Mono<ResponseEntity<CustomerResponse>> result = customerRestControllerAdapter
                .createCustomers(Mono.just(createRequest), null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertEquals(testResponse, response.getBody());
                })
                .verifyComplete();

        verify(customerRestMapper, times(1)).toDomain(any(CreateCustomerRequest.class));
        verify(customerService, times(1)).createCustomer(any(Customer.class));
        verify(customerRestMapper, times(1)).toResponse(any(Customer.class));
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        Customer customer2 = testCustomer.toBuilder()
                .personId(UUID.randomUUID())
                .name("Maria Lopez")
                .build();

        CustomerResponse response2 = new CustomerResponse();
        response2.setCustomerId(customer2.getPersonId());
        response2.setName(customer2.getName());

        when(customerService.getCustomers()).thenReturn(Flux.just(testCustomer, customer2));
        when(customerRestMapper.toResponse(testCustomer)).thenReturn(testResponse);
        when(customerRestMapper.toResponse(customer2)).thenReturn(response2);

        Mono<ResponseEntity<Flux<CustomerResponse>>> result = customerRestControllerAdapter
                .getAllCustomer(null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    StepVerifier.create(response.getBody())
                            .expectNext(testResponse)
                            .expectNext(response2)
                            .verifyComplete();
                })
                .verifyComplete();

        verify(customerService, times(1)).getCustomers();
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        UUID customerId = testCustomer.getPersonId();
        when(customerService.getOnlyCustomerById(customerId.toString())).thenReturn(Mono.just(testCustomer));
        when(customerRestMapper.toResponse(testCustomer)).thenReturn(testResponse);

        Mono<ResponseEntity<CustomerResponse>> result = customerRestControllerAdapter
                .getCustomerById(customerId, null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(testResponse, response.getBody());
                })
                .verifyComplete();

        verify(customerService, times(1)).getOnlyCustomerById(customerId.toString());
        verify(customerRestMapper, times(1)).toResponse(testCustomer);
    }

    @Test
    @DisplayName("Should return 404 when customer not found")
    void shouldReturn404WhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        when(customerService.getOnlyCustomerById(customerId.toString()))
                .thenReturn(Mono.error(new CustomerNotFoundException(customerId.toString())));

        Mono<ResponseEntity<CustomerResponse>> result = customerRestControllerAdapter
                .getCustomerById(customerId, null);

        StepVerifier.create(result)
                .expectError(CustomerNotFoundException.class)
                .verify();

        verify(customerService, times(1)).getOnlyCustomerById(customerId.toString());
        verify(customerRestMapper, never()).toResponse(any(Customer.class));
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        UUID customerId = testCustomer.getPersonId();
        Customer updatedCustomer = testCustomer.toBuilder()
                .address("Nueva Direccion")
                .build();

        when(customerRestMapper.toUpdateDomain(any(UpdateCustomerRequest.class))).thenReturn(updatedCustomer);
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(Mono.just(updatedCustomer));
        when(customerRestMapper.toResponse(any(Customer.class))).thenReturn(testResponse);

        Mono<ResponseEntity<CustomerResponse>> result = customerRestControllerAdapter
                .updateCustomer(customerId, Mono.just(updateRequest), null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals(testResponse, response.getBody());
                })
                .verifyComplete();

        verify(customerRestMapper, times(1)).toUpdateDomain(any(UpdateCustomerRequest.class));
        verify(customerService, times(1)).updateCustomer(any(Customer.class));
        verify(customerRestMapper, times(1)).toResponse(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        UUID customerId = testCustomer.getPersonId();
        when(customerService.deleteCustomer(customerId.toString())).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> result = customerRestControllerAdapter
                .deleteCustomer(customerId, null);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
                })
                .verifyComplete();

        verify(customerService, times(1)).deleteCustomer(customerId.toString());
    }
}
