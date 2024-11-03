# Stage 1: Maven Build Environment
FROM maven:3.8.5-openjdk-17 AS builder

# Set up workspace
WORKDIR /app

# Build the main user-service application
COPY ../user-service /app/user-service
WORKDIR /app/user-service
RUN mvn clean package -DskipTests

# Stage 2: Runtime Environment
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled JAR from the builder stage
COPY --from=builder /app/user-service/target/*.jar /app/user-service.jar

# Run the user-service application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
