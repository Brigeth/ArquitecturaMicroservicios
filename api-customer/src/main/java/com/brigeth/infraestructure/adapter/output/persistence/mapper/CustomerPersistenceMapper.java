package com.brigeth.infraestructure.adapter.output.persistence.mapper;

import com.brigeth.domain.models.Customer;
import com.brigeth.infraestructure.adapter.output.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CustomerPersistenceMapper {
    @Mapping(target = "id", ignore = true)
    CustomerEntity toEntity(Customer customer);

    @Mapping(source = "id", target = "personId")
    Customer toDomain(CustomerEntity customerEntity);
}
