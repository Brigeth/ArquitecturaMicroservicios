package com.brigeth.domain.models;

import com.brigeth.domain.enums.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "The name can only contain letters and spaces")
    @Size(min = 5, max = 100, message = "The name must contain at least a first and last name")
    private String name;
    
    @NotNull(message = "Gender is required")
    private GenderType gender;
    
    @NotBlank(message = "Identification is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "The identification must have 10 digits")
    private String identification;
    
    @NotBlank(message = "The address is required")
    @Size(min = 5, message = "The address must be at least 5 characters long")
    private String address;
    
    @NotBlank(message = "The phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "The phone number must have 10 digits")
    private String phone;

    public void normalize() {
        this.name = normalizeName(this.name);
        this.identification = normalizeIdentification(this.identification);
        this.phone = normalizePhone(this.phone);
        this.address = normalizeAddress(this.address);
    }

    protected String normalizeName(String name) {
        if (name == null) return null;
        
        // Remove multiple spaces and normalize
        String normalized = name.trim().replaceAll("\\s+", " ");
        
        // Convert the first letter of each word to a capital letter
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
