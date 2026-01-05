package com.btoapanta.account.service.infrastructure.adapter.ouput.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    
    @JsonProperty("customerId")
    private UUID id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("gender")
    private String gender;
    
    @JsonProperty("age")
    private Integer age;
    
    @JsonProperty("identification")
    private String identification;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("state")
    private Boolean state;
}
