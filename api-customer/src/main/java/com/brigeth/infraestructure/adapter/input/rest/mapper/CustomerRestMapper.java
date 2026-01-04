package com.brigeth.infraestructure.adapter.input.rest.mapper;


import com.brigeth.customer.infrastructure.adapter.input.rest.model.CreateCustomerRequest;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.CustomerResponse;
import com.brigeth.customer.infrastructure.adapter.input.rest.model.UpdateCustomerRequest;
import com.brigeth.domain.enums.GenderType;
import com.brigeth.domain.models.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CustomerRestMapper {

    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "gender", qualifiedByName = "normalizeGender")
    @Mapping(target = "state", constant = "true")
    Customer toDomain(CreateCustomerRequest customerRequest);

    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "gender", qualifiedByName = "normalizeGender")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "state", ignore = true)
    Customer toUpdateDomain(UpdateCustomerRequest customerRequest);

    @Mapping(source = "personId", target = "customerId")
    CustomerResponse toResponse(Customer customer);

    @Named("normalizeGender")
    default GenderType normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return null;
        }
        return GenderType.valueOf(gender.toUpperCase().trim());
    }
}
