# Pruebas Unitarias - API Customer

Este proyecto incluye pruebas unitarias completas utilizando **JUnit 5**, **Mockito** y **Jacoco** para la cobertura de código.

## 🧪 Tecnologías de Testing

- **JUnit 5**: Framework de pruebas unitarias
- **Mockito**: Framework para crear mocks y stubs
- **Reactor Test**: Soporte para pruebas reactivas con Project Reactor
- **Jacoco**: Herramienta de cobertura de código

## 📋 Pruebas Implementadas

### 1. CustomerServiceImplTest
Pruebas unitarias para el servicio de clientes:
- ✅ Crear cliente exitosamente
- ✅ Validar identificación duplicada al crear
- ✅ Validar datos inválidos al crear
- ✅ Obtener todos los clientes
- ✅ Obtener lista vacía cuando no hay clientes
- ✅ Obtener cliente por ID
- ✅ Fallar al obtener cliente inexistente
- ✅ Eliminar cliente exitosamente
- ✅ Fallar al eliminar cliente inexistente
- ✅ Actualizar cliente exitosamente
- ✅ Fallar al actualizar cliente inexistente
- ✅ Fallar al actualizar con identificación duplicada
- ✅ Fallar al actualizar con datos inválidos

**Cobertura**: ~100% de los métodos del servicio

### 2. ValidationServiceImplTest
Pruebas unitarias para el servicio de validación:
- ✅ Validar identificación única exitosamente
- ✅ Lanzar excepción con identificación duplicada
- ✅ Permitir identificación duplicada para el mismo cliente
- ✅ Detectar identificación duplicada en otro cliente
- ✅ Validar cuando no hay clientes
- ✅ Validar que el cliente existe
- ✅ Lanzar excepción cuando el cliente no existe
- ✅ Mapear errores del repositorio
- ✅ Validar múltiples clientes con identificaciones únicas

**Cobertura**: ~100% de los métodos del servicio

### 3. CustomerTest
Pruebas unitarias para el modelo de dominio Customer:
- ✅ Crear cliente válido
- ✅ Normalizar y validar cliente
- ✅ Validar longitud mínima de contraseña (8 caracteres)
- ✅ Validar longitud máxima de contraseña (20 caracteres)
- ✅ Validar contraseña con mayúsculas
- ✅ Validar contraseña con minúsculas
- ✅ Validar contraseña con números
- ✅ Validar contraseña no nula o vacía
- ✅ Validar estado no nulo
- ✅ Validar estado como true/false
- ✅ Usar builder correctamente
- ✅ Usar toBuilder correctamente
- ✅ Validar campos heredados de Person
- ✅ Contraseñas en límites válidos

**Cobertura**: ~100% del modelo Customer

### 4. PersonTest
Pruebas unitarias para el modelo de dominio Person:
- ✅ Crear persona válida
- ✅ Normalizar nombre correctamente
- ✅ Normalizar identificación correctamente
- ✅ Normalizar teléfono correctamente
- ✅ Normalizar dirección correctamente
- ✅ Validar nombre no nulo o vacío
- ✅ Validar nombre con al menos dos palabras
- ✅ Validar nombre solo con letras
- ✅ Aceptar nombres con tildes y ñ
- ✅ Validar género no nulo
- ✅ Validar identificación no nula o vacía
- ✅ Validar identificación solo números
- ✅ Validar identificación con 10 dígitos
- ✅ Validar dirección no nula o vacía
- ✅ Validar dirección con mínimo 5 caracteres
- ✅ Validar teléfono no nulo o vacío
- ✅ Validar teléfono solo números
- ✅ Validar teléfono con 10 dígitos
- ✅ Capitalizar nombres correctamente

**Cobertura**: ~100% del modelo Person

## 🚀 Ejecutar Pruebas

### Ejecutar todas las pruebas
```bash
./gradlew test
```

### Ejecutar pruebas con reporte de cobertura
```bash
./gradlew test jacocoTestReport
```

### Ver reporte de cobertura
El reporte HTML se genera en:
```
build/reports/jacoco/test/html/index.html
```

### Verificar cobertura mínima
```bash
./gradlew jacocoTestCoverageVerification
```
El proyecto está configurado para requerir un **70% de cobertura mínima**.

## 📊 Configuración de Jacoco

El plugin Jacoco está configurado en `build.gradle`:

```gradle
jacoco {
    toolVersion = "0.8.12"
}

jacocoTestReport {
    dependsOn test
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.70  // 70% cobertura mínima
            }
        }
    }
}
```

## 🧩 Estructura de Pruebas

```
src/test/java/com/brigeth/
├── application/
│   └── service/
│       └── impl/
│           ├── CustomerServiceImplTest.java
│           └── ValidationServiceImplTest.java
└── domain/
    └── models/
        ├── CustomerTest.java
        └── PersonTest.java
```

## 🎯 Buenas Prácticas Implementadas

1. **AAA Pattern**: Arrange-Act-Assert en todas las pruebas
2. **Mocks apropiados**: Uso de `@Mock` para dependencias
3. **Inyección automática**: Uso de `@InjectMocks` para la clase bajo prueba
4. **Nombres descriptivos**: Tests con `@DisplayName` claros
5. **Pruebas parametrizadas**: Uso de `@ParameterizedTest` para múltiples casos
6. **StepVerifier**: Para pruebas reactivas con Reactor
7. **Verificaciones de Mockito**: Validar interacciones con mocks
8. **Cobertura de casos edge**: Límites, nulos, vacíos, etc.

## 📈 Métricas de Cobertura Esperadas

- **Servicios**: >90% de cobertura
- **Modelos de dominio**: >95% de cobertura
- **General**: >70% de cobertura (mínimo requerido)

## 🔍 Tipos de Pruebas

### Pruebas de Flujo Exitoso
Validan que los casos de uso normales funcionen correctamente.

### Pruebas de Casos de Error
Validan el manejo correcto de excepciones y errores.

### Pruebas de Validación
Validan las reglas de negocio del dominio.

### Pruebas de Normalización
Validan que los datos se normalicen correctamente.

### Pruebas de Límites
Validan valores en los límites de las reglas de negocio.

## 🛠️ Comandos Útiles

```bash
# Ejecutar solo un test específico
./gradlew test --tests CustomerServiceImplTest

# Ejecutar tests con más detalle
./gradlew test --info

# Limpiar y ejecutar tests
./gradlew clean test jacocoTestReport

# Ver tareas disponibles
./gradlew tasks
```

## 📝 Notas Adicionales

- Las pruebas utilizan **StepVerifier** de Reactor Test para validar flujos reactivos
- Se usan **mocks** para aislar las unidades bajo prueba
- Las pruebas son **independientes** y pueden ejecutarse en cualquier orden
- Se incluyen **pruebas parametrizadas** para validar múltiples casos similares
- El reporte de Jacoco incluye métricas de líneas, ramas y complejidad ciclomática

## 🎓 Ejemplo de Uso de StepVerifier

```java
StepVerifier.create(customerService.createCustomer(testCustomer))
    .expectNext(testCustomer)
    .verifyComplete();
```

## 🎓 Ejemplo de Uso de Mockito

```java
when(customerPersistencePort.saveCustomer(any(Customer.class)))
    .thenReturn(Mono.just(testCustomer));

verify(customerPersistencePort, times(1))
    .saveCustomer(any(Customer.class));
```
