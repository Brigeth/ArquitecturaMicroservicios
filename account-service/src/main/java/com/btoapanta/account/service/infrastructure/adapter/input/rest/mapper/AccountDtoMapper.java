package com.btoapanta.account.service.infrastructure.adapter.input.rest.mapper;

import com.btoapanta.account.service.domain.model.Account;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountCreateRequest;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountResponse;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountType;
import com.btoapanta.account.service.infrastructure.input.adapter.rest.account.service.models.AccountUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AccountDtoMapper {

    @Mapping(source = "initialBalance", target = "balance", qualifiedByName = "doubleToBigDecimal")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "apiToDomainAccountType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movements", ignore = true)
    Account toDomain(AccountCreateRequest request);

    @Mapping(source = "balance", target = "balance", qualifiedByName = "bigDecimalToDouble")
    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "domainToApiAccountType")
    AccountResponse toResponse(Account account);


    @Mapping(source = "accountType", target = "accountType", qualifiedByName = "apiToDomainAccountType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "movements", ignore = true)
    void updateDomainFromDto(AccountUpdateRequest request, @MappingTarget Account account);

    @Named("domainToApiAccountType")
    default AccountType domainToApiAccountType(com.btoapanta.account.service.domain.enums.AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return AccountType.valueOf(accountType.name());
    }


    @Named("apiToDomainAccountType")
    default com.btoapanta.account.service.domain.enums.AccountType apiToDomainAccountType(AccountType accountType) {
        if (accountType == null) {
            return null;
        }
        return com.btoapanta.account.service.domain.enums.AccountType.valueOf(accountType.name());
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
}
