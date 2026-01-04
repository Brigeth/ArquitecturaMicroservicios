package com.brigeth.infraestructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;
    private String name;
    private String gender;
    private String identification;
    private String address;
    private String phone;
    private String password;
    private Boolean state;

    public CustomerEntity() {
        this.id = UUID.randomUUID().toString();
    }
}
