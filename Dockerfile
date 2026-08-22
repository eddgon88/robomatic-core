# Stage 1: Build Spring Boot JAR
FROM gradle:8.9-jdk17 AS build

WORKDIR /app/src
COPY . /app/src

RUN gradle bootJar --no-daemon -x test

# Stage 2: Minimal Production JRE Image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Cloud Run dynamic port specification (defaults to 8080)
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

COPY --from=build /app/src/build/libs/robomatic-core-0.0.1.jar /app/robomatic-core-0.0.1.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Duser.timezone=UTC -Dserver.port=${PORT} -jar /app/robomatic-core-0.0.1.jar"]
