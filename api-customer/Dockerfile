# ========================================
# Compilación (Builder)
# ========================================
FROM gradle:8.5-jdk17-alpine AS builder
# Directorio de trabajo
WORKDIR /app
# Copia archivos de configuración de Gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
# Copia el código fuente
COPY src ./src
# Compila la aplicación (sin daemon para ahorrar memoria)
RUN gradle build --no-daemon -x test
# ========================================
# Ejecución (Runtime)
# ========================================
FROM eclipse-temurin:17-jre-alpine
# Directorio de trabajo
WORKDIR /app
# Copia SOLO el jar compilado desde la etapa builder
COPY --from=builder /app/build/libs/api-customer.jar app.jar
# Expone el puerto por defecto de Spring Boot
EXPOSE 8080
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
