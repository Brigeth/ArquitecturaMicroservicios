package com.btoapanta.account.service.domain.model;

import com.btoapanta.account.service.domain.enums.MovementType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    private UUID id;
    
    @NotNull(message = "Account number is required")
    @Min(value = 100000L, message = "Account number must be at least 100000")
    @Max(value = 9999999999L, message = "Account number cannot exceed 9999999999")
    private Long accountNumber;
    
    @NotNull(message = "Movement type is required")
    private MovementType movementType;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    @NotNull(message = "Balance before is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance before cannot be negative")
    private BigDecimal balanceBefore;
    
    @NotNull(message = "Balance after is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance after cannot be negative")
    private BigDecimal balanceAfter;
    
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDateTime date;
}

