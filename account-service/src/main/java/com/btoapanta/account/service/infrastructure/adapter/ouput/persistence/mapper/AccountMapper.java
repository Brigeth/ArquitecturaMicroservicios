package com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.mapper;

import com.btoapanta.account.service.domain.model.Account;
import com.btoapanta.account.service.infrastructure.adapter.ouput.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {MovementMapper.class})
public interface AccountMapper {

    @Mapping(source = "movements", target = "movementEntityList")
    AccountEntity toEntity (Account account);

    @Mapping(source = "movementEntityList", target = "movements")
    Account ToDomain (AccountEntity accountEntity);

}
