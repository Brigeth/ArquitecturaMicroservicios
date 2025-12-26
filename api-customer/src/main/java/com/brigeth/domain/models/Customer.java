package com.brigeth.domain.models;


import com.brigeth.domain.exception.ValidationException;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Person {
    private String password;
    private Boolean state;

    /**
     * Normaliza y valida todos los campos del cliente (incluye Person)
     */
    @Override
    public void normalizeAndValidate() {
        super.normalizeAndValidate();
        validatePassword(this.password);
        validateState(this.state);
    }

    /**
     * Valida solo el cliente (sin validar Person)
     */
    public void validateCustomer() {
        validatePassword(this.password);
        validateState(this.state);
    }

    // ==================== VALIDACIONES ====================

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("La contraseña es obligatoria");
        }

        if (password.length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres");
        }

        if (password.length() > 20) {
            throw new ValidationException("La contraseña no puede exceder 20 caracteres");
        }

        // Al menos una mayúscula
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("La contraseña debe contener al menos una letra mayúscula");
        }

        // Al menos una minúscula
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("La contraseña debe contener al menos una letra minúscula");
        }

        // Al menos un número
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("La contraseña debe contener al menos un número");
        }
    }

    private void validateState(Boolean state) {
        if (state == null) {
            throw new ValidationException("El estado es obligatorio");
        }
    }
}
