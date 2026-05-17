# -------- BUILD STAGE --------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# copy everything
COPY proto-module /app/proto-module
COPY kitchen-service /app/kitchen-service

# build proto module first
RUN mvn -f /app/proto-module/pom.xml clean install

# build kitchen service
RUN mvn -f /app/kitchen-service/pom.xml clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/kitchen-service/target/*.jar app.jar

EXPOSE 50052

ENTRYPOINT ["java", "-jar", "app.jar"]