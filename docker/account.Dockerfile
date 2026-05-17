# -------- BUILD STAGE --------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# copy everything
COPY proto-module /app/proto-module
COPY account-service /app/account-service

# build proto module first
RUN mvn -f /app/proto-module/pom.xml clean install

# build account service
RUN mvn -f /app/account-service/pom.xml clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/account-service/target/*.jar app.jar

EXPOSE 50053

ENTRYPOINT ["java", "-jar", "app.jar"]