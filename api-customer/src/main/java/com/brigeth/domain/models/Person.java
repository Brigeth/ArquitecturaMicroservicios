package com.brigeth.domain.models;

import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private UUID personId;
    private String name;
    private GenderType gender;
    private String identification;
    private String address;
    private String phone;

    public void normalizeAndValidate() {
        this.name = normalizeName(this.name);
        this.identification = normalizeIdentification(this.identification);
        this.phone = normalizePhone(this.phone);
        this.address = normalizeAddress(this.address);
        validate();
    }

    public void validate() {
        validateName(this.name);
        validateGender(this.gender);
        validateIdentification(this.identification);
        validateAddress(this.address);
        validatePhone(this.phone);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name is required");
        }

        String normalized = name.trim().replaceAll("\\s+", " ");

        String[] parts = normalized.split(" ");
        if (parts.length < 2) {
            throw new ValidationException("The name must contain at least a first and last name.");
        }

        if (!normalized.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new ValidationException("The name can only contain letters and spaces.");
        }
    }

    private void validateGender(GenderType gender) {
        if (gender == null) {
            throw new ValidationException("Gender is required");
        }
    }

    private void validateIdentification(String identification) {
        if (identification == null || identification.isBlank()) {
            throw new ValidationException("Identification is mandatory");
        }

        String normalized = identification.trim();

        if (!normalized.matches("^\\d+$")) {
            throw new ValidationException("The identification must contain only numbers.");
        }

        if (normalized.length() != 10) {
            throw new ValidationException("The identification must have 10 digits.");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new ValidationException("The address is required");
        }

        if (address.trim().length() < 5) {
            throw new ValidationException("The address must be at least 5 characters long");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("The phone is required");
        }

        String normalized = phone.trim().replaceAll("[\\s-]", "");

        if (!normalized.matches("^\\d+$")) {
            throw new ValidationException("The phone number should only contain numbers.");
        }

        // Debe tener exactamente 10 dígitos
        if (normalized.length() != 10) {
            throw new ValidationException("The phone number must have 10 digits.");
        }
    }

    protected String normalizeName(String name) {
        if (name == null) return null;
        
        // Eliminar espacios múltiples y normalizar
        String normalized = name.trim().replaceAll("\\s+", " ");
        
        // Capitalizar primera letra de cada palabra
        StringBuilder result = new StringBuilder();
        String[] words = normalized.split(" ");
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1));
                if (i < words.length - 1) {
                    result.append(" ");
                }
            }
        }
        
        return result.toString();
    }

    protected String normalizeIdentification(String identification) {
        if (identification == null) return null;
        return identification.trim().replaceAll("[\\s-]", "");
    }

    protected String normalizePhone(String phone) {
        if (phone == null) return null;
        return phone.trim().replaceAll("[\\s\\-()]", "");
    }

    protected String normalizeAddress(String address) {
        if (address == null) return null;
        return address.trim().replaceAll("\\s+", " ");
    }
}
