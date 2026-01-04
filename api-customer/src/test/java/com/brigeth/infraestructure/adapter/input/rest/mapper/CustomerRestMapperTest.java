package com.brigeth.infraestructure.adapter.input.rest.mapper;

import com.brigeth.customer.infrastructure.adapter.input.rest.model.CreateCustomerRequest;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CustomerResponse;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.UpdateCustomerRequest;
import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.models.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerRestMapper - Unit Tests")
class CustomerRestMapperTest {

    private CustomerRestMapper customerRestMapper;

    private CreateCustomerRequest createRequest;
    private UpdateCustomerRequest updateRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRestMapper = Mappers.getMapper(CustomerRestMapper.class);
        
        createRequest = new CreateCustomerRequest();
        createRequest.setName("Juan Perez");
        createRequest.setGender(CreateCustomerRequest.GenderEnum.M);
        createRequest.setIdentification("1234567890");
        createRequest.setAddress("Calle Principal 123");
        createRequest.setPhone("0987654321");
        createRequest.setPassword("SecurePass123");

        updateRequest = new UpdateCustomerRequest();
        updateRequest.setName("Juan Perez Updated");
        updateRequest.setGender(UpdateCustomerRequest.GenderEnum.M);
        updateRequest.setIdentification("1234567890");
        updateRequest.setAddress("Nueva Direccion");
        updateRequest.setPhone("0987654321");

        customer = Customer.builder()
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
    @DisplayName("Should map CreateCustomerRequest to Customer domain")
    void shouldMapCreateRequestToDomain() {
        // When
        Customer result = customerRestMapper.toDomain(createRequest);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez", result.getName());
        assertEquals(GenderType.M, result.getGender());
        assertEquals("1234567890", result.getIdentification());
        assertEquals("Calle Principal 123", result.getAddress());
        assertEquals("0987654321", result.getPhone());
        assertEquals("SecurePass123", result.getPassword());
        assertTrue(result.getState());
        assertNull(result.getPersonId());
    }

    @Test
    @DisplayName("Should map UpdateCustomerRequest to Customer domain")
    void shouldMapUpdateRequestToDomain() {
        // When
        Customer result = customerRestMapper.toUpdateDomain(updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Juan Perez Updated", result.getName());
        assertEquals(GenderType.M, result.getGender());
        assertEquals("1234567890", result.getIdentification());
        assertEquals("Nueva Direccion", result.getAddress());
        assertEquals("0987654321", result.getPhone());
        assertNull(result.getPassword()); // password is ignored in update
        assertNull(result.getState()); // state is ignored in update
        assertNull(result.getPersonId());
    }

    @Test
    @DisplayName("Should map Customer domain to CustomerResponse")
    void shouldMapDomainToResponse() {
        // When
        CustomerResponse result = customerRestMapper.toResponse(customer);

        // Then
        assertNotNull(result);
        assertEquals(customer.getPersonId(), result.getCustomerId());
        assertEquals("Juan Perez", result.getName());
        assertEquals(CustomerResponse.GenderEnum.M, result.getGender());
        assertEquals("1234567890", result.getIdentification());
        assertEquals("Calle Principal 123", result.getAddress());
        assertEquals("0987654321", result.getPhone());
    }

    @Test
    @DisplayName("Should map gender F correctly in CreateRequest")
    void shouldMapFemaleGenderInCreateRequest() {
        // Given
        createRequest.setGender(CreateCustomerRequest.GenderEnum.F);

        // When
        Customer result = customerRestMapper.toDomain(createRequest);

        // Then
        assertEquals(GenderType.F, result.getGender());
    }

    @Test
    @DisplayName("Should map gender F correctly in UpdateRequest")
    void shouldMapFemaleGenderInUpdateRequest() {
        // Given
        updateRequest.setGender(UpdateCustomerRequest.GenderEnum.F);

        // When
        Customer result = customerRestMapper.toUpdateDomain(updateRequest);

        // Then
        assertEquals(GenderType.F, result.getGender());
    }

    @Test
    @DisplayName("Should map gender F correctly in Response")
    void shouldMapFemaleGenderInResponse() {
        // Given
        customer = customer.toBuilder().gender(GenderType.F).build();

        // When
        CustomerResponse result = customerRestMapper.toResponse(customer);

        // Then
        assertEquals(CustomerResponse.GenderEnum.F, result.getGender());
    }

    @Test
    @DisplayName("Should handle null values in CreateRequest")
    void shouldHandleNullValuesInCreateRequest() {
        // Given
        CreateCustomerRequest nullRequest = new CreateCustomerRequest();

        // When
        Customer result = customerRestMapper.toDomain(nullRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.getState()); // state is set to constant true
    }
}
