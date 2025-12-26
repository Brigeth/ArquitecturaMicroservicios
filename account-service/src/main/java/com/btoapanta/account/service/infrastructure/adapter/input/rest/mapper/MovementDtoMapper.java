package com.btoapanta.account.service.infrastructure.adapter.input.rest.mapper;

import com.btoapanta.account.service.domain.model.Movement;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementCreateRequest;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementResponse;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.MovementType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


@Mapper(componentModel = "spring")
public interface MovementDtoMapper {


    @Mapping(source = "amount", target = "amount", qualifiedByName = "doubleToBigDecimal")
    @Mapping(source = "movementType", target = "movementType", qualifiedByName = "apiToDomainMovementType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balanceBefore", ignore = true)
    @Mapping(target = "balanceAfter", ignore = true)
    @Mapping(target = "date", ignore = true)
    Movement toDomain(MovementCreateRequest request);


    @Mapping(source = "id", target = "movementId")
    @Mapping(source = "date", target = "date", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(source = "amount", target = "amount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(source = "balanceBefore", target = "balanceBefore", qualifiedByName = "bigDecimalToDouble")
    @Mapping(source = "balanceAfter", target = "balanceAfter", qualifiedByName = "bigDecimalToDouble")
    @Mapping(source = "movementType", target = "movementType", qualifiedByName = "domainToApiMovementType")
    MovementResponse toResponse(Movement movement);

    @Named("domainToApiMovementType")
    default MovementType domainToApiMovementType(com.btoapanta.account.service.domain.enums.MovementType movementType) {
        if (movementType == null) {
            return null;
        }
        return MovementType.valueOf(movementType.name());
    }

    @Named("apiToDomainMovementType")
    default com.btoapanta.account.service.domain.enums.MovementType apiToDomainMovementType(MovementType movementType) {
        if (movementType == null) {
            return null;
        }
        return com.btoapanta.account.service.domain.enums.MovementType.valueOf(movementType.name());
    }

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(Double value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }

    @Named("bigDecimalToDouble")
    default Double bigDecimalToDouble(BigDecimal value) {
        if (value == null) {
            return 0.0;
        }
        return value.doubleValue();
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    @Named("offsetDateTimeToLocalDateTime")
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
}
