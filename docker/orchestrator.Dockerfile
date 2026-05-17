# -------- BUILD STAGE --------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# copy shared proto + orchestrator service
COPY proto-module /app/proto-module
COPY orchestrator-service /app/orchestrator-service

# build proto first
RUN mvn -f /app/proto-module/pom.xml clean install

# build orchestrator
RUN mvn -f /app/orchestrator-service/pom.xml clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/orchestrator-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]