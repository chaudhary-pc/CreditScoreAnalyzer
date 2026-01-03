# --- Stage 1: Build the application using Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Create the final lightweight image ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
