FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/naming-server-0.0.1-SNAPSHOT.jar ./naming-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "naming-server.jar"]
