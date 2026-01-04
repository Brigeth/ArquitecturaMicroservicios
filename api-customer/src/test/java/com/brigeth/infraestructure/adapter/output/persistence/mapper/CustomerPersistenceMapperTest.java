package com.brigeth.infraestructure.adapter.output.persistence.mapper;

import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.models.Customer;
import com.brigeth.infraestructure.adapter.output.persistence.entity.CustomerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerPersistenceMapper - Unit Tests")
class CustomerPersistenceMapperTest {

    private CustomerPersistenceMapper customerPersistenceMapper;

    private Customer customer;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        customerPersistenceMapper = Mappers.getMapper(CustomerPersistenceMapper.class);
        
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

        customerEntity = new CustomerEntity();
        customerEntity.setId(UUID.randomUUID());
        customerEntity.setName("Maria Lopez");
        customerEntity.setGender("F");
        customerEntity.setIdentification("0987654321");
        customerEntity.setAddress("Avenida Central 456");
        customerEntity.setPhone("0912345678");
        customerEntity.setPassword("Password456");
        customerEntity.setState(true);
    }

    @Test
    @DisplayName("Should map Customer domain to CustomerEntity")
    void shouldMapDomainToEntity() {
        CustomerEntity result = customerPersistenceMapper.toEntity(customer);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Juan Perez", result.getName());
        assertEquals("M", result.getGender());
        assertEquals("1234567890", result.getIdentification());
        assertEquals("Calle Principal 123", result.getAddress());
        assertEquals("0987654321", result.getPhone());
        assertEquals("SecurePass123", result.getPassword());
        assertTrue(result.getState());
    }

    @Test
    @DisplayName("Should map CustomerEntity to Customer domain")
    void shouldMapEntityToDomain() {
        Customer result = customerPersistenceMapper.toDomain(customerEntity);

        assertNotNull(result);
        assertEquals(customerEntity.getId(), result.getPersonId());
        assertEquals("Maria Lopez", result.getName());
        assertEquals(GenderType.F, result.getGender());
        assertEquals("0987654321", result.getIdentification());
        assertEquals("Avenida Central 456", result.getAddress());
        assertEquals("0912345678", result.getPhone());
        assertEquals("Password456", result.getPassword());
        assertTrue(result.getState());
    }

    @Test
    @DisplayName("Should map gender M correctly from domain to entity")
    void shouldMapMaleGenderFromDomainToEntity() {
        customer = customer.toBuilder().gender(GenderType.M).build();

        CustomerEntity result = customerPersistenceMapper.toEntity(customer);

        assertEquals("M", result.getGender());
    }

    @Test
    @DisplayName("Should map gender F correctly from domain to entity")
    void shouldMapFemaleGenderFromDomainToEntity() {
        customer = customer.toBuilder().gender(GenderType.F).build();

        CustomerEntity result = customerPersistenceMapper.toEntity(customer);

        assertEquals("F", result.getGender());
    }

    @Test
    @DisplayName("Should map gender M correctly from entity to domain")
    void shouldMapMaleGenderFromEntityToDomain() {
        customerEntity.setGender("M");

        Customer result = customerPersistenceMapper.toDomain(customerEntity);

        assertEquals(GenderType.M, result.getGender());
    }

    @Test
    @DisplayName("Should map gender F correctly from entity to domain")
    void shouldMapFemaleGenderFromEntityToDomain() {
        customerEntity.setGender("F");

        Customer result = customerPersistenceMapper.toDomain(customerEntity);

        assertEquals(GenderType.F, result.getGender());
    }

    @Test
    @DisplayName("Should handle state false correctly")
    void shouldHandleStateFalse() {
        customer = customer.toBuilder().state(false).build();

        CustomerEntity result = customerPersistenceMapper.toEntity(customer);

        assertFalse(result.getState());
    }

    @Test
    @DisplayName("Should preserve all fields in roundtrip conversion")
    void shouldPreserveFieldsInRoundtrip() {
        CustomerEntity entity = customerPersistenceMapper.toEntity(customer);
        Customer roundtrip = customerPersistenceMapper.toDomain(entity);

        assertEquals(customer.getName(), roundtrip.getName());
        assertEquals(customer.getGender(), roundtrip.getGender());
        assertEquals(customer.getIdentification(), roundtrip.getIdentification());
        assertEquals(customer.getAddress(), roundtrip.getAddress());
        assertEquals(customer.getPhone(), roundtrip.getPhone());
        assertEquals(customer.getPassword(), roundtrip.getPassword());
        assertEquals(customer.getState(), roundtrip.getState());
    }
}
