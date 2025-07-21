FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# Pre-cache dependencias de Maven
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

# Copiamos sólo el código fuente
COPY src ./src

# Construimos el JAR sin tests (y cacheamos deps)
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# curl para health-check
RUN apk add --no-cache curl

# Copiamos el JAR generado
COPY --from=build /app/target/*stats*.jar app.jar

EXPOSE 8080

# Health-check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
