FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/user-recommendation-service-0.0.1-SNAPSHOT.jar ./user-recommendation-service.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "user-recommendation-service.jar"]
