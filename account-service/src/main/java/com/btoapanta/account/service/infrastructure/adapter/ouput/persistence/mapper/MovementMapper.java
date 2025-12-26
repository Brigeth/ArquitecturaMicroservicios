package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.mapper;


import com.btoapanta.account.service.domain.model.Movement;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.AccountEntity;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MovementMapper {

    @Mapping(source = "date", target = "createdDate")
    @Mapping(target = "account", ignore = true)
    MovementEntity toEntity(Movement movement);

    @Mapping(source = "createdDate", target = "date")
    @Mapping(source = "account.accountNumber", target = "accountNumber")
    Movement toDomain(MovementEntity movementEntity);
}
