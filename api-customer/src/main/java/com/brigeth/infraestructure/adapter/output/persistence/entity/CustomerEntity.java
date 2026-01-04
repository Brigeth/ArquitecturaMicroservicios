package com.brigeth.infraestructure.adapter.output.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customers")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String gender;
    private String identification;
    private String address;
    private String phone;
    private String password;
    private Boolean state;

}
