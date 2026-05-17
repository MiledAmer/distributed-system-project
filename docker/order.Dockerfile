# -------- BUILD STAGE --------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# copy everything (important)
COPY proto-module /app/proto-module
COPY order-service /app/order-service

# build proto first
RUN mvn -f /app/proto-module/pom.xml clean install

# build service
RUN mvn -f /app/order-service/pom.xml clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/order-service/target/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]