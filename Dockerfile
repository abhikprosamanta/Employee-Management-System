FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -DskipTests package

FROM amazoncorretto:17-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/employee-management-system-0.0.1-SNAPSHOT.jar app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
