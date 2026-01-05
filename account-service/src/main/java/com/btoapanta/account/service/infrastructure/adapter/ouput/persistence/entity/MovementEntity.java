package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity;


import com.btoapanta.account.service.domain.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movements")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @NotNull(message = "Account is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @NotNull(message = "Movement type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @NotNull(message = "Balance before is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance before cannot be negative")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceBefore;
    
    @NotNull(message = "Balance after is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance after cannot be negative")
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @PastOrPresent(message = "Date cannot be in the future")
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
