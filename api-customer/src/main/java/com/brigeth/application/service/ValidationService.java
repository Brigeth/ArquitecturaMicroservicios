package com.brigeth.application.service;

import reactor.core.publisher.Mono;

public interface ValidationService {
    
    /**
     * Valida que la identificación sea única en el sistema
     * @param identification Identificación a validar
     * @param excludeCustomerId ID del cliente a excluir (null para creación)
     * @return Mono vacío si es válida, error si ya existe
     */
    Mono<Void> validateUniqueIdentification(String identification, String excludeCustomerId);
    
    /**
     * Valida que el cliente existe en el sistema
     * @param customerId ID del cliente
     * @return Mono vacío si existe, error si no existe
     */
    Mono<Void> validateCustomerExists(String customerId);
}
