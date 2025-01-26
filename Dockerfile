# Stage 1: Build custom Java runtime using jlink
FROM eclipse-temurin:21 as jre-build

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

# Stage 2: Build the application
FROM eclipse-temurin:21-jdk-alpine AS build

RUN apk add --no-cache maven

COPY src /app/src
COPY pom.xml /app

WORKDIR /app
RUN mvn clean package -DskipTests -U \
    && rm -rf /root/.m2 \
    && rm -rf /app/src

# Stage 3: Create the final image with the custom Java runtime and application
FROM debian:buster-slim
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

# Copy the application JAR from the build stage
WORKDIR /usr/src/app
COPY --from=build /app/target/QualityLabPro-0.7.jar ./app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

# Expose the application port
EXPOSE ${SERVER_PORT}
