FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk add --no-cache maven

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests -U \
    && rm -rf /root/.m2

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/QualityLabPro-0.7.jar app.jar