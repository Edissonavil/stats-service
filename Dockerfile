# syntax=docker/dockerfile:1.4   # opcional, no hace daño

FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# Pre-descargar dependencias
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copiar el código
COPY src ./src

# Compilar sin tests
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/target/*stats*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
