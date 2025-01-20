FROM maven:3.9.8-eclipse-temurin-21 AS build

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests -U

FROM openjdk:21
WORKDIR /usr/src/app

COPY --from=build app/target/QualityLabPro-0.7.jar app.jar
