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


    @Override
    public void normalizeAndValidate() {
        super.normalizeAndValidate();
        validatePassword(this.password);
        validateState(this.state);
    }

    public void validateCustomer() {
        validatePassword(this.password);
        validateState(this.state);
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ValidationException("A password is required.");
        }

        if (password.length() < 8) {
            throw new ValidationException("The password must be at least 8 characters long");
        }

        if (password.length() > 20) {
            throw new ValidationException("The password cannot exceed 20 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("The password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("The password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("The password must contain at least one number");
        }
    }

    private void validateState(Boolean state) {
        if (state == null) {
            throw new ValidationException("The state is mandatory");
        }
    }
}
