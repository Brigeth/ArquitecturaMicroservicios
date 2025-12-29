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

@DisplayName("Person - Pruebas Unitarias")
class PersonTest {

    @Test
    @DisplayName("Debe crear una persona válida")
    void shouldCreateValidPerson() {

        Person person = Person.builder()
                .personId(UUID.randomUUID())
                .name("Juan Perez Lopez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        assertNotNull(person);
        assertEquals("Juan Perez Lopez", person.getName());
        assertEquals(GenderType.M, person.getGender());
    }

    @Test
    @DisplayName("Debe normalizar el nombre correctamente")
    void shouldNormalizeNameCorrectly() {
        Person person = Person.builder()
                .name("  juan   perez   lopez  ")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("Juan Perez Lopez", person.getName());
    }

    @Test
    @DisplayName("Debe normalizar la identificación correctamente")
    void shouldNormalizeIdentificationCorrectly() {

        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification(" 1234-567-890 ")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("1234567890", person.getIdentification());
    }

    @Test
    @DisplayName("Debe normalizar el teléfono correctamente")
    void shouldNormalizePhoneCorrectly() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone(" 098-765-4321 ")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("0987654321", person.getPhone());
    }

    @Test
    @DisplayName("Debe normalizar la dirección correctamente")
    void shouldNormalizeAddressCorrectly() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("  Calle   Principal   123  ")
                .phone("0987654321")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("Calle Principal 123", person.getAddress());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Debe validar que el nombre no sea nulo o vacío")
    void shouldValidateNameNotNullOrBlank(String name) {
        Person person = Person.builder()
                .name(name)
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El nombre es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que el nombre contenga al menos nombre y apellido")
    void shouldValidateNameContainsAtLeastTwoWords() {
        Person person = Person.builder()
                .name("Juan")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El nombre debe contener al menos nombre y apellido", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que el nombre solo contenga letras")
    void shouldValidateNameOnlyContainsLetters() {
        Person person = Person.builder()
                .name("Juan123 Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El nombre solo puede contener letras y espacios", exception.getMessage());
    }

    @Test
    @DisplayName("Debe aceptar nombres con tildes y ñ")
    void shouldAcceptNamesWithAccentsAndÑ() {
        Person person = Person.builder()
                .name("José María Peña")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        assertDoesNotThrow(person::validate);
        assertEquals("José María Peña", person.getName());
    }

    @Test
    @DisplayName("Debe validar que el género no sea nulo")
    void shouldValidateGenderNotNull() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(null)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El género es obligatorio", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Debe validar que la identificación no sea nula o vacía")
    void shouldValidateIdentificationNotNullOrBlank(String identification) {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification(identification)
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("La identificación es obligatoria", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la identificación contenga solo números")
    void shouldValidateIdentificationOnlyContainsNumbers() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("12345ABC90")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("La identificación debe contener solo números", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la identificación tenga exactamente 10 dígitos")
    void shouldValidateIdentificationHasExactly10Digits() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("123456789")  // Solo 9 dígitos
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("La identificación debe tener 10 dígitos", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Debe validar que la dirección no sea nula o vacía")
    void shouldValidateAddressNotNullOrBlank(String address) {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address(address)
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("La dirección es obligatoria", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que la dirección tenga al menos 5 caracteres")
    void shouldValidateAddressMinimumLength() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Call")  // Solo 4 caracteres
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("La dirección debe tener al menos 5 caracteres", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Debe validar que el teléfono no sea nulo o vacío")
    void shouldValidatePhoneNotNullOrBlank(String phone) {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone(phone)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El teléfono es obligatorio", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que el teléfono contenga solo números")
    void shouldValidatePhoneOnlyContainsNumbers() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("098ABC4321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El teléfono debe contener solo números", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar que el teléfono tenga exactamente 10 dígitos")
    void shouldValidatePhoneHasExactly10Digits() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("098765432")  // Solo 9 dígitos
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("El teléfono debe tener 10 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Debe validar todos los campos correctamente")
    void shouldValidateAllFieldsCorrectly() {
        Person person = Person.builder()
                .personId(UUID.randomUUID())
                .name("Juan Perez Lopez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        assertDoesNotThrow(person::validate);
    }

    @Test
    @DisplayName("Debe usar builder correctamente para género femenino")
    void shouldUseBuilderForFemaleGender() {
        Person person = Person.builder()
                .personId(UUID.randomUUID())
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Avenida Central 456")
                .phone("0912345678")
                .build();

        // Then
        assertEquals("Maria Lopez", person.getName());
        assertEquals(GenderType.F, person.getGender());
        assertDoesNotThrow(person::validate);
    }

    @Test
    @DisplayName("Debe normalizar nombre con múltiples espacios")
    void shouldNormalizeNameWithMultipleSpaces() {
        Person person = Person.builder()
                .name("   JUAN    CARLOS    PEREZ   ")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("Juan Carlos Perez", person.getName());
    }

    @Test
    @DisplayName("Debe capitalizar correctamente nombres en minúsculas")
    void shouldCapitalizeNamesInLowercase() {
        Person person = Person.builder()
                .name("juan perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        // When
        person.normalizeAndValidate();

        // Then
        assertEquals("Juan Perez", person.getName());
    }

    @Test
    @DisplayName("Debe aceptar identificación en el límite exacto")
    void shouldAcceptIdentificationAtExactBoundary() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("0123456789")  // Exactamente 10 dígitos
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        assertDoesNotThrow(person::validate);
        assertEquals("0123456789", person.getIdentification());
    }

    @Test
    @DisplayName("Debe aceptar dirección en el límite mínimo")
    void shouldAcceptAddressAtMinimumBoundary() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle")  // Exactamente 5 caracteres
                .phone("0987654321")
                .build();

        assertDoesNotThrow(person::validate);
    }
}
