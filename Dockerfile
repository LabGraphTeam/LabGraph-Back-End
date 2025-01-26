# First stage, build the custom JRE
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

COPY src /app/src
COPY pom.xml /app

WORKDIR /app

ENV MAVEN_VERSION 3.5.4
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN apk update && \
    apk add --no-cache tar binutils maven

RUN mvn clean package -DskipTests -U \
    && rm -rf /root/.m2 \
    && rm -rf /app/src

RUN jar xvf target/QualityLabPro-0.7.jar
RUN jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    target/QualityLabPro-0.7.jar > modules.txt

# Build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules $(cat modules.txt) \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=zip-6 \
         --output /optimized-jdk-21

# Second stage, Use the custom JRE and build the app image
FROM alpine:latest
ENV JAVA_HOME=/opt/jdk/jdk-21
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=jre-builder /optimized-jdk-21 $JAVA_HOME

WORKDIR /usr/src/app
COPY --from=jre-builder /app/target/QualityLabPro-0.7.jar ./app.jar


# Get Gmail SMTP server certificate
RUN apk add --no-cache openssl && \
    openssl s_client -connect smtp.gmail.com:587 -showcerts </dev/null 2>/dev/null | \
    sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > gmail.cert

# Import the certificate into the Java keystore
RUN keytool -import -alias smtp.gmail.com -keystore "$JAVA_HOME/lib/security/cacerts" -file gmail.cert -storepass changeit -noprompt


ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080

EXPOSE ${SERVER_PORT}