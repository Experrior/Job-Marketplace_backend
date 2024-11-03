# Stage 1: Maven Build Environment
FROM maven:3.8.5-openjdk-17 AS builder

# Set up workspace
WORKDIR /app

# Build the main notification-service application
COPY ../notification-service /app/notification-service
WORKDIR /app/notification-service
RUN mvn clean package -DskipTests

# Stage 2: Runtime Environment
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR from the builder stage
COPY --from=builder /app/notification-service/target/*.jar /app/notification-service.jar

# Run the notification-service application
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
