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

    /**
     * Normaliza y valida todos los campos de la persona
     */
    public void normalizeAndValidate() {
        this.name = normalizeName(this.name);
        this.identification = normalizeIdentification(this.identification);
        this.phone = normalizePhone(this.phone);
        this.address = normalizeAddress(this.address);
        validate();
    }

    /**
     * Valida que todos los campos cumplan las reglas de negocio
     */
    public void validate() {
        validateName(this.name);
        validateGender(this.gender);
        validateIdentification(this.identification);
        validateAddress(this.address);
        validatePhone(this.phone);
    }

    // ==================== VALIDACIONES ====================

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("El nombre es obligatorio");
        }

        String normalized = name.trim().replaceAll("\\s+", " ");
        
        // Debe contener al menos nombre y apellido
        String[] parts = normalized.split(" ");
        if (parts.length < 2) {
            throw new ValidationException("El nombre debe contener al menos nombre y apellido");
        }

        // Solo letras y espacios (incluye acentos y ñ)
        if (!normalized.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            throw new ValidationException("El nombre solo puede contener letras y espacios");
        }
    }

    private void validateGender(GenderType gender) {
        if (gender == null) {
            throw new ValidationException("El género es obligatorio");
        }
    }

    private void validateIdentification(String identification) {
        if (identification == null || identification.isBlank()) {
            throw new ValidationException("La identificación es obligatoria");
        }

        String normalized = identification.trim();

        // Debe contener solo números
        if (!normalized.matches("^\\d+$")) {
            throw new ValidationException("La identificación debe contener solo números");
        }

        // Debe tener exactamente 10 dígitos
        if (normalized.length() != 10) {
            throw new ValidationException("La identificación debe tener 10 dígitos");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new ValidationException("La dirección es obligatoria");
        }

        if (address.trim().length() < 5) {
            throw new ValidationException("La dirección debe tener al menos 5 caracteres");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("El teléfono es obligatorio");
        }

        String normalized = phone.trim().replaceAll("[\\s-]", "");

        // Debe contener solo números
        if (!normalized.matches("^\\d+$")) {
            throw new ValidationException("El teléfono debe contener solo números");
        }

        // Debe tener exactamente 10 dígitos
        if (normalized.length() != 10) {
            throw new ValidationException("El teléfono debe tener 10 dígitos");
        }
    }

    // ==================== NORMALIZACIÓN ====================

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
        // Eliminar espacios y guiones
        return identification.trim().replaceAll("[\\s-]", "");
    }

    protected String normalizePhone(String phone) {
        if (phone == null) return null;
        // Eliminar espacios, guiones y paréntesis
        return phone.trim().replaceAll("[\\s\\-()]", "");
    }

    protected String normalizeAddress(String address) {
        if (address == null) return null;
        // Eliminar espacios múltiples
        return address.trim().replaceAll("\\s+", " ");
    }
}
