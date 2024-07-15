# crea el jar de la aplicacion en una imagen temporal
FROM openjdk:17-alpine AS build

RUN mkdir -p /app/src
WORKDIR /app/src
COPY . /app/src

RUN ./gradlew bootjar

# crea una imagen ligera solo con el jar de spring boot
FROM openjdk:17-alpine

# Definir variables de entorno
ENV DB_HOST=docker-postgresql
ENV DB_PORT=5432
ENV DB_USER=robomatic
ENV DB_PWD=robomatic
ENV RABBITMQ_ADMIN_HOST=rabbitmq
ENV RABBITMQ_ADMIN_PORT=5672
ENV RABBITMQ_ADMIN_LOGIN=admin
ENV RABBITMQ_ADMIN_PASSWORD=admin
ENV SPRING_PROFILES_ACTIVE=local

COPY --from=build /app/src/build/libs/robomatic-core-0.0.1.jar /app/

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=UTC", "-Dspring.profiles.active=local", "-jar","/app/robomatic-core-0.0.1.jar"]
