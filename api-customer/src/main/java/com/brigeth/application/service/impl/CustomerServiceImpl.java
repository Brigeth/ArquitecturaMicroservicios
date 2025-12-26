package com.brigeth.application.service.impl;


import com.brigeth.application.service.CustomerService;
import com.brigeth.application.service.ValidationService;
import com.brigeth.domain.models.Customer;
import com.brigeth.domain.port.output.CustomerPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerPersistencePort customerPersistencePort;
    private final ValidationService validationService;

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        log.info("Creando cliente con identificación: {}", customer.getIdentification());
        customer.normalizeAndValidate();
        return validationService.validateUniqueIdentification(customer.getIdentification(), null)
            .then(customerPersistencePort.saveCustomer(customer))
            .doOnSuccess(c -> log.info("Cliente creado exitosamente: {}", c.getPersonId()))
            .doOnError(e -> log.error("Error al crear cliente: {}", e.getMessage()));
    }

    @Override
    public Flux<Customer> getCustomers() {
        log.debug("Obteniendo todos los clientes");
        return customerPersistencePort.getAllCustomers();
    }

    @Override
    public Mono<Customer> getOnlyCustomerById(String customerId) {
        log.info("Buscando cliente: {}", customerId);
        return customerPersistencePort.getCustomerById(customerId)
                .doOnSuccess(c -> log.debug("Cliente encontrado: {}", customerId))
                .doOnError(e -> log.warn("Cliente no encontrado: {}", customerId));
    }

    @Override
    public Mono<Void> deleteCustomer(String customerId) {
        log.info("Eliminando cliente: {}", customerId);
        
        // Validar que el cliente existe antes de eliminar
        return validationService.validateCustomerExists(customerId)
            .then(customerPersistencePort.deleteCustomer(customerId))
            .doOnSuccess(v -> log.info("Cliente eliminado exitosamente: {}", customerId))
            .doOnError(e -> log.error("Error al eliminar cliente: {}", e.getMessage()));
    }

    @Override
    public Mono<Customer> updateCustomer(Customer customer) {
        log.info("Actualizando cliente: {}", customer.getPersonId());
        
        // Normalizar y validar el modelo de dominio
        customer.normalizeAndValidate();
        
        // Validar que existe y que la identificación es única
        return validationService.validateCustomerExists(customer.getPersonId().toString())
            .then(validationService.validateUniqueIdentification(
                customer.getIdentification(), 
                customer.getPersonId().toString()
            ))
            .then(customerPersistencePort.updateCustomer(customer))
            .doOnSuccess(c -> log.info("Cliente actualizado exitosamente: {}", c.getPersonId()))
            .doOnError(e -> log.error("Error al actualizar cliente: {}", e.getMessage()));
    }
}
