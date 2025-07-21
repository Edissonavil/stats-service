FROM maven:3.9.7-eclipse-temurin-21 AS build
WORKDIR /app

# 1️⃣ Pre-cache dependencias de Maven
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

# 2️⃣ Copiamos solo el código fuente
COPY src ./src

# 3️⃣ Construimos el JAR sin tests y cacheamos deps
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

############################
# 🚀 Runtime stage (Alpine) #
############################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 4️⃣ curl para health-check
RUN apk add --no-cache curl

# 5️⃣ Copiamos el JAR generado
COPY --from=build /app/target/*stats*.jar app.jar

EXPOSE 8080

# 6️⃣ Health-check interno
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=prod"]
