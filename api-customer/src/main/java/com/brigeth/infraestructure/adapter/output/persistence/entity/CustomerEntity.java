package com.brigeth.infraestructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^[MFO]$", message = "Gender must be M, F, or O")
    @Column(nullable = false, length = 1)
    private String gender;
    
    @NotBlank(message = "Identification is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Identification must have 10 digits")
    @Column(nullable = false, unique = true, length = 10)
    private String identification;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone must have 10 digits")
    @Column(nullable = false, length = 10)
    private String phone;
    
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;
    
    @NotNull(message = "State is mandatory")
    @Column(nullable = false)
    private Boolean state;

}
