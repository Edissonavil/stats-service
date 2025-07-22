# syntax=docker/dockerfile:1.4

# ─── Fase de construcción (Build) ───
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copiar solo el POM primero (para cachear dependencias)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 2. Copiar el código fuente
COPY src ./src

# 3. Compilar (sin tests)
RUN mvn -B package -DskipTests

# ─── Fase de ejecución (Runtime) ───
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Instalar curl para health checks
RUN apk add --no-cache curl

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]