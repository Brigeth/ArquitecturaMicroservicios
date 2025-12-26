package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity;

import com.btoapanta.account.service.domain.enums.AccountType;
import jakarta.persistence.*;
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
    private Long accountNumber;
    private UUID customerId;
    private String customerName;
    private AccountType accountType;
    private BigDecimal balance;
    private Boolean state;
    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER  //JOIN AUTOMÁTICO
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
