package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity;

import com.btoapanta.account.service.domain.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull(message = "Account number is required")
    @Min(value = 100000L, message = "Account number must be at least 100000")
    @Max(value = 9999999999L, message = "Account number cannot exceed 9999999999")
    @Column(name = "account_number", nullable = false, unique = true, columnDefinition = "BIGINT")
    private Long accountNumber;
    
    @NotNull(message = "Customer ID is required")
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID customerId;
    
    @NotBlank(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;
    
    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @NotNull(message = "State is required")
    @Column(nullable = false)
    private Boolean state;
    
    @Valid
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER  //automatic join
    )
    @Builder.Default
    private List<MovementEntity> movementEntityList = new ArrayList<>();

    public void addMovement(MovementEntity movementEntity) {
        movementEntityList.add(movementEntity);
        movementEntity.setAccount(this);
    }

    public void removeMovement(MovementEntity movementEntity) {
        movementEntityList.remove(movementEntity);
        movementEntity.setAccount(null);
    }

}
