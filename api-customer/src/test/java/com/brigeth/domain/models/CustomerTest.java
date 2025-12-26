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

@DisplayName("Customer - Pruebas Unitarias")
class CustomerTest {

    @Test
    @DisplayName("Debe crear un cliente válido")
    void shouldCreateValidCustomer() {
        // Given & When
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

        // Then
        assertNotNull(customer);
        assertEquals("Juan Perez Lopez", customer.getName());
        assertEquals("SecurePass123", customer.getPassword());
        assertTrue(customer.getState());
    }

    @Test
    @DisplayName("Debe normalizar y validar un cliente correctamente")
    void shouldNormalizeAndValidateCustomer() {
        // Given
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
    @DisplayName("Debe validar contraseña con longitud mínima")
    void shouldValidatePasswordMinimumLength() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123")  // Solo 7 caracteres
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar contraseña con longitud máxima")
    void shouldValidatePasswordMaximumLength() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass1234567890123")  // 25 caracteres
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña no puede exceder 20 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la contraseña contenga al menos una mayúscula")
    void shouldValidatePasswordContainsUppercase() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("securepass123")  // Sin mayúsculas
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña debe contener al menos una letra mayúscula", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la contraseña contenga al menos una minúscula")
    void shouldValidatePasswordContainsLowercase() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SECUREPASS123")  // Sin minúsculas
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña debe contener al menos una letra minúscula", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la contraseña contenga al menos un número")
    void shouldValidatePasswordContainsNumber() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePassword")  // Sin números
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña debe contener al menos un número", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Debe validar que la contraseña no sea nula o vacía")
    void shouldValidatePasswordNotNullOrBlank(String password) {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password(password)
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("La contraseña es obligatoria", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que el estado no sea nulo")
    void shouldValidateStateNotNull() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(null)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("El estado es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar estado como true")
    void shouldValidateStateAsTrue() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        // When & Then
        assertDoesNotThrow(customer::validateCustomer);
        assertTrue(customer.getState());
    }

    @Test
    @DisplayName("Debe validar estado como false")
    void shouldValidateStateAsFalse() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(false)
                .build();

        // When & Then
        assertDoesNotThrow(customer::validateCustomer);
        assertFalse(customer.getState());
    }

    @Test
    @DisplayName("Debe usar builder correctamente")
    void shouldUseBuilderCorrectly() {
        // Given
        UUID id = UUID.randomUUID();

        // When
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

        // Then
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
    @DisplayName("Debe usar toBuilder correctamente")
    void shouldUseToBuilderCorrectly() {
        // Given
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

        // When
        Customer modified = original.toBuilder()
                .address("Nueva Direccion 789")
                .phone("0999999999")
                .build();

        // Then
        assertEquals(original.getPersonId(), modified.getPersonId());
        assertEquals(original.getName(), modified.getName());
        assertEquals("Nueva Direccion 789", modified.getAddress());
        assertEquals("0999999999", modified.getPhone());
    }

    @Test
    @DisplayName("Debe validar todos los campos heredados de Person")
    void shouldValidateAllFieldsFromPerson() {
        // Given
        Customer customer = Customer.builder()
                .name("J")  // Nombre inválido
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("SecurePass123")
                .state(true)
                .build();

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
                customer::normalizeAndValidate);
        assertEquals("El nombre debe contener al menos nombre y apellido", exception.getMessage());
    }

    @Test
    @DisplayName("Debe aceptar contraseña válida en el límite inferior")
    void shouldAcceptPasswordAtMinimumBoundary() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123A")  // Exactamente 8 caracteres
                .state(true)
                .build();

        // When & Then
        assertDoesNotThrow(customer::validateCustomer);
    }

    @Test
    @DisplayName("Debe aceptar contraseña válida en el límite superior")
    void shouldAcceptPasswordAtMaximumBoundary() {
        // Given
        Customer customer = Customer.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .password("Pass123456789012345A")  // Exactamente 20 caracteres
                .state(true)
                .build();

        // When & Then
        assertDoesNotThrow(customer::validateCustomer);
    }
}
