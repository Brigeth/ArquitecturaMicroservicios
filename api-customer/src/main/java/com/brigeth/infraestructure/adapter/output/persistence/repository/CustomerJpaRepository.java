package com.brigeth.infraestructure.adapter.output.persistence.repository;

import com.brigeth.infraestructure.adapter.output.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerJpaRepository extends JpaRepository <CustomerEntity, String>{
    //la clave primaria de la entidad CustomerEntity es un String

}
