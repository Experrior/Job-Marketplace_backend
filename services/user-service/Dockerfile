FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

COPY user-service/pom.xml .
RUN mvn dependency:go-offline

COPY user-service/src ./src
RUN mvn clean install -Dmaven.test.skip=true -X

FROM openjdk:21-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/app.jar

CMD java -Djdk.tls.rejectClientInitiatedRenegotiation=true -jar /app/app.jar
