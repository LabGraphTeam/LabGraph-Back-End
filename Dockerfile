COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests -U \
    && rm -rf /root/.m2

# Run stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /usr/src/app
COPY --from=build /app/target/QualityLabPro-0.7.jar ./app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

EXPOSE ${SERVER_PORT}