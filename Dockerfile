# ---------- Build Stage ----------
FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos SOLO el módulo users-service
COPY stats-service/pom.xml stats-service/
RUN mvn -f stats-service/pom.xml dependency:go-offline -B

# Copiamos el código del módulo
COPY stats-service/ stats-service/
RUN mvn -f stats-service/pom.xml -B package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el jar del módulo users-service
COPY --from=build /app/stats-service/target/stats-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
