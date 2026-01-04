package com.brigeth.infraestructure.adapter.output.persistence;

import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.CustomerNotFoundException;
import com.brigeth.domain.models.Customer;
import com.brigeth.infraestructure.adapter.output.persistence.entity.CustomerEntity;
import com.brigeth.infraestructure.adapter.output.persistence.mapper.CustomerPersistenceMapper;
import com.brigeth.infraestructure.adapter.output.persistence.repository.CustomerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerPersistenceAdapter - Unit Tests")
class CustomerPersistenceAdapterTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    @Mock
    private CustomerPersistenceMapper customerPersistenceMapper;

    @InjectMocks
    private CustomerPersistenceAdapter customerPersistenceAdapter;

    private Customer testCustomer;
    private CustomerEntity testCustomerEntity;

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

        testCustomerEntity = new CustomerEntity();
        testCustomerEntity.setId(testCustomer.getPersonId().toString());
        testCustomerEntity.setName(testCustomer.getName());
        testCustomerEntity.setIdentification(testCustomer.getIdentification());
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        CustomerEntity entity2 = new CustomerEntity();
        entity2.setId(UUID.randomUUID().toString());
        entity2.setName("Maria Lopez");
        
        Customer customer2 = Customer.builder()
                .personId(UUID.fromString(entity2.getId()))
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Av Central 456")
                .phone("0912345678")
                .password("Pass456")
                .state(true)
                .build();

        List<CustomerEntity> entities = Arrays.asList(testCustomerEntity, entity2);

        when(customerJpaRepository.findAll()).thenReturn(entities);
        when(customerPersistenceMapper.toDomain(testCustomerEntity)).thenReturn(testCustomer);
        when(customerPersistenceMapper.toDomain(entity2)).thenReturn(customer2);

        StepVerifier.create(customerPersistenceAdapter.getAllCustomers())
                .expectNext(testCustomer)
                .expectNext(customer2)
                .verifyComplete();

        verify(customerJpaRepository, times(1)).findAll();
        verify(customerPersistenceMapper, times(2)).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should return empty flux when no customers exist")
    void shouldReturnEmptyFluxWhenNoCustomers() {
        when(customerJpaRepository.findAll()).thenReturn(List.of());

        StepVerifier.create(customerPersistenceAdapter.getAllCustomers())
                .verifyComplete();

        verify(customerJpaRepository, times(1)).findAll();
        verify(customerPersistenceMapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        when(customerJpaRepository.findById(customerId)).thenReturn(Optional.of(testCustomerEntity));
        when(customerPersistenceMapper.toDomain(testCustomerEntity)).thenReturn(testCustomer);

        StepVerifier.create(customerPersistenceAdapter.getCustomerById(customerId))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerJpaRepository, times(1)).findById(customerId);
        verify(customerPersistenceMapper, times(1)).toDomain(testCustomerEntity);
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer not found")
    void shouldThrowExceptionWhenCustomerNotFound() {
        String customerId = UUID.randomUUID().toString();
        when(customerJpaRepository.findById(customerId)).thenReturn(Optional.empty());

        StepVerifier.create(customerPersistenceAdapter.getCustomerById(customerId))
                .expectError(CustomerNotFoundException.class)
                .verify();

        verify(customerJpaRepository, times(1)).findById(customerId);
        verify(customerPersistenceMapper, never()).toDomain(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("Should save customer successfully")
    void shouldSaveCustomerSuccessfully() {
        when(customerPersistenceMapper.toEntity(testCustomer)).thenReturn(testCustomerEntity);
        when(customerJpaRepository.save(testCustomerEntity)).thenReturn(testCustomerEntity);
        when(customerPersistenceMapper.toDomain(testCustomerEntity)).thenReturn(testCustomer);

        StepVerifier.create(customerPersistenceAdapter.saveCustomer(testCustomer))
                .expectNext(testCustomer)
                .verifyComplete();

        verify(customerPersistenceMapper, times(1)).toEntity(testCustomer);
        verify(customerJpaRepository, times(1)).save(testCustomerEntity);
        verify(customerPersistenceMapper, times(1)).toDomain(testCustomerEntity);
    }

    @Test
    @DisplayName("Should delete customer successfully")
    void shouldDeleteCustomerSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        doNothing().when(customerJpaRepository).deleteById(customerId);

        StepVerifier.create(customerPersistenceAdapter.deleteCustomer(customerId))
                .verifyComplete();

        verify(customerJpaRepository, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        String customerId = testCustomer.getPersonId().toString();
        Customer updatedCustomer = testCustomer.toBuilder()
                .address("Nueva Direccion 789")
                .build();

        CustomerEntity existingEntity = new CustomerEntity();
        existingEntity.setId(customerId);
        existingEntity.setName(testCustomer.getName());
        existingEntity.setGender(testCustomer.getGender().toString());
        existingEntity.setIdentification(testCustomer.getIdentification());
        existingEntity.setAddress(testCustomer.getAddress());
        existingEntity.setPhone(testCustomer.getPhone());
        existingEntity.setPassword(testCustomer.getPassword());
        existingEntity.setState(testCustomer.getState());

        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(customerId);
        savedEntity.setName(updatedCustomer.getName());
        savedEntity.setGender(updatedCustomer.getGender().toString());
        savedEntity.setIdentification(updatedCustomer.getIdentification());
        savedEntity.setAddress("Nueva Direccion 789");
        savedEntity.setPhone(updatedCustomer.getPhone());
        savedEntity.setPassword(updatedCustomer.getPassword());
        savedEntity.setState(updatedCustomer.getState());

        when(customerJpaRepository.findById(customerId)).thenReturn(Optional.of(existingEntity));
        when(customerJpaRepository.save(any(CustomerEntity.class))).thenReturn(savedEntity);
        when(customerPersistenceMapper.toDomain(savedEntity)).thenReturn(updatedCustomer);

        StepVerifier.create(customerPersistenceAdapter.updateCustomer(updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();

        verify(customerJpaRepository, times(1)).findById(customerId);
        verify(customerJpaRepository, times(1)).save(any(CustomerEntity.class));
        verify(customerPersistenceMapper, times(1)).toDomain(savedEntity);
    }
}
