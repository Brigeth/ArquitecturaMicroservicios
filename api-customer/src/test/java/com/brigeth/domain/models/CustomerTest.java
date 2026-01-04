package com.brigeth.domain.models;

import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer - Unit Tests")
class CustomerTest {

    @Test
    @DisplayName("You must create a valid client")
    void shouldCreateValidCustomer() {
        Customer customer = Customer.builder()
                .personId(UUID.randomUUID())
                .name("Juan Perez Lopez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();
        assertNotNull(customer);
        assertEquals("Juan Perez Lopez", customer.getName());
        assertEquals("SecurePass123", customer.getPassword());
        assertTrue(customer.getState());
    }

    @Test
    @DisplayName("You must normalize and validate a client correctly")
    void shouldNormalizeAndValidateCustomer() {
        Customer customer = Customer.builder()
                .name("  juan   perez  ")
                .gender(GenderType.M)
                .identification(" 1234567890 ")
                .address("  Calle   Principal   123  ")
                .phone(" 098-765-4321 ")
                .password("SecurePass123")
                .state(true)
                .build();

        // When
        assertDoesNotThrow(customer::normalizeAndValidate);

        // Then
        assertEquals("Juan Perez", customer.getName());
        assertEquals("1234567890", customer.getIdentification());
        assertEquals("Calle Principal 123", customer.getAddress());
        assertEquals("0987654321", customer.getPhone());
    }

    @Test
    @DisplayName("You must validate password with minimum length")
    void shouldValidatePasswordMinimumLength() {

        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123")  // Solo 7 caracteres
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("You must validate the password with the maximum length.")
    void shouldValidatePasswordMaximumLength() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass1234567890123")  // 25 caracteres
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The password cannot exceed 20 characters", exception.getMessage());
    }

    @Test
    @DisplayName("You must validate that the password contains at least one uppercase letter")
    void shouldValidatePasswordContainsUppercase() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("securepass123")  // Sin mayúsculas
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The password must contain at least one uppercase letter", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that password contains at least one lowercase letter")
    void shouldValidatePasswordContainsLowercase() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SECUREPASS123")  // Sin minúsculas
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The password must contain at least one lowercase letter", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that password contains at least one number")
    void shouldValidatePasswordContainsNumber() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePassword")  // Sin números
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The password must contain at least one number", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should validate that password is not null or blank")
    void shouldValidatePasswordNotNullOrBlank(String password) {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password(password)
                .state(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("A password is required.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that state is not null")
    void shouldValidateStateNotNull() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The state is mandatory", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate state as true")
    void shouldValidateStateAsTrue() {
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        assertDoesNotThrow(customer::validateCustomer);
        assertTrue(customer.getState());
    }

    @Test
    @DisplayName("Should validate state as false")
    void shouldValidateStateAsFalse() {

        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(false)
                .build();

        assertDoesNotThrow(customer::validateCustomer);
        assertFalse(customer.getState());
    }

    @Test
    @DisplayName("Should use builder correctly")
    void shouldUseBuilderCorrectly() {

        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .personId(id)
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Avenida Central 456")
                .phone("0912345678")
                .password("Password456")
                .state(true)
                .build();
        assertEquals(id, customer.getPersonId());
        assertEquals("Maria Lopez", customer.getName());
        assertEquals(GenderType.F, customer.getGender());
        assertEquals("0987654321", customer.getIdentification());
        assertEquals("Avenida Central 456", customer.getAddress());
        assertEquals("0912345678", customer.getPhone());
        assertEquals("Password456", customer.getPassword());
        assertTrue(customer.getState());
    }

    @Test
    @DisplayName("Should use toBuilder correctly")
    void shouldUseToBuilderCorrectly() {

        Customer original = Customer.builder()
                .personId(UUID.randomUUID())
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        Customer modified = original.toBuilder()
                .address("Nueva Direccion 789")
                .phone("0999999999")
                .build();

        assertEquals(original.getPersonId(), modified.getPersonId());
        assertEquals(original.getName(), modified.getName());
        assertEquals("Nueva Direccion 789", modified.getAddress());
        assertEquals("0999999999", modified.getPhone());
    }

    @Test
    @DisplayName("Should validate all fields inherited from Person")
    void shouldValidateAllFieldsFromPerson() {

        Customer customer = Customer.builder()
                .name("J")  // Nombre inválido
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();


        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("The name must contain at least a first and last name.", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept valid password at minimum boundary")
    void shouldAcceptPasswordAtMinimumBoundary() {

        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123A")  // Exactamente 8 caracteres
                .state(true)
                .build();


        assertDoesNotThrow(customer::validateCustomer);
    }

    @Test
    @DisplayName("Should accept valid password at maximum boundary")
    void shouldAcceptPasswordAtMaximumBoundary() {

        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123456789012345A")  // Exactamente 20 caracteres
                .state(true)
                .build();

        assertDoesNotThrow(customer::validateCustomer);
    }
}
