# ========================================
# Build Stage (Builder)
# ========================================
FROM gradle:8.5-jdk17-alpine AS builder
# Working directory
WORKDIR /app
# Copy Gradle configuration files
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
# Copy source code
COPY src ./src
# Build the application (without daemon to save memory)
RUN gradle build --no-daemon -x test
# ========================================
# Runtime Stage
# ========================================
FROM eclipse-temurin:17-jre-alpine
# Working directory
WORKDIR /app
# Copy ONLY the compiled jar from builder stage
COPY --from=builder /app/build/libs/api-customer.jar app.jar
# Expose Spring Boot default port
EXPOSE 8080
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
