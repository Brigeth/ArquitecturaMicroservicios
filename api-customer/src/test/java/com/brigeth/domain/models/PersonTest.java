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

@DisplayName("Person - Unit Tests")
class PersonTest {

    @Test
    @DisplayName("Should create a valid person")
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
    @DisplayName("Should normalize name correctly")
    void shouldNormalizeNameCorrectly() {
        Person person = Person.builder()
                .name("  juan   perez   lopez  ")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        person.normalizeAndValidate();

        assertEquals("Juan Perez Lopez", person.getName());
    }

    @Test
    @DisplayName("Should normalize identification correctly")
    void shouldNormalizeIdentificationCorrectly() {

        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification(" 1234-567-890 ")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        person.normalizeAndValidate();

        assertEquals("1234567890", person.getIdentification());
    }

    @Test
    @DisplayName("Should normalize phone correctly")
    void shouldNormalizePhoneCorrectly() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone(" 098-765-4321 ")
                .build();

        person.normalizeAndValidate();

        assertEquals("0987654321", person.getPhone());
    }

    @Test
    @DisplayName("Should normalize address correctly")
    void shouldNormalizeAddressCorrectly() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("  Calle   Principal   123  ")
                .phone("0987654321")
                .build();

        person.normalizeAndValidate();

        assertEquals("Calle Principal 123", person.getAddress());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should validate that name is not null or blank")
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
        assertEquals("Name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that name contains at least first and last name")
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
        assertEquals("The name must contain at least a first and last name.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that name only contains letters")
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
        assertEquals("The name can only contain letters and spaces.", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept names with accents and ñ")
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
    @DisplayName("Should validate that gender is not null")
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
        assertEquals("Gender is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should validate that identification is not null or blank")
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
        assertEquals("Identification is mandatory", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that identification only contains numbers")
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
        assertEquals("The identification must contain only numbers.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that identification has exactly 10 digits")
    void shouldValidateIdentificationHasExactly10Digits() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("123456789")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("The identification must have 10 digits.", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should validate that address is not null or blank")
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
        assertEquals("The address is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that address has at least 5 characters")
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
        assertEquals("The address must be at least 5 characters long", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Should validate that phone is not null or blank")
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
        assertEquals("The phone is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that phone only contains numbers")
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
        assertEquals("The phone number should only contain numbers.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate that phone has exactly 10 digits")
    void shouldValidatePhoneHasExactly10Digits() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("098765432")
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                person::validate);
        assertEquals("The phone number must have 10 digits.", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate all fields correctly")
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
    @DisplayName("Should use builder correctly for female gender")
    void shouldUseBuilderForFemaleGender() {
        Person person = Person.builder()
                .personId(UUID.randomUUID())
                .name("Maria Lopez")
                .gender(GenderType.F)
                .identification("0987654321")
                .address("Avenida Central 456")
                .phone("0912345678")
                .build();

        assertEquals("Maria Lopez", person.getName());
        assertEquals(GenderType.F, person.getGender());
        assertDoesNotThrow(person::validate);
    }

    @Test
    @DisplayName("Should normalize name with multiple spaces")
    void shouldNormalizeNameWithMultipleSpaces() {
        Person person = Person.builder()
                .name("   JUAN    CARLOS    PEREZ   ")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        person.normalizeAndValidate();

        assertEquals("Juan Carlos Perez", person.getName());
    }

    @Test
    @DisplayName("Should capitalize names in lowercase correctly")
    void shouldCapitalizeNamesInLowercase() {
        Person person = Person.builder()
                .name("juan perez")
                .gender(GenderType.M)
                .identification("1234567890")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        person.normalizeAndValidate();

        assertEquals("Juan Perez", person.getName());
    }

    @Test
    @DisplayName("Should accept identification at exact boundary")
    void shouldAcceptIdentificationAtExactBoundary() {
        Person person = Person.builder()
                .name("Juan Perez")
                .gender(GenderType.M)
                .identification("0123456789")
                .address("Calle Principal 123")
                .phone("0987654321")
                .build();

        assertDoesNotThrow(person::validate);
        assertEquals("0123456789", person.getIdentification());
    }

    @Test
    @DisplayName("Should accept address at minimum boundary")
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
