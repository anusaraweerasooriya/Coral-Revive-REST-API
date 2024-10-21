# Stage 1: Build
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Copy any required static files to the build context
COPY kg_final.txt .
COPY ratings_final.txt .
COPY user_mapping.json .
COPY item_mapping.json .
COPY post_mapping.json .

# Run the Maven build
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/user-recommendation-service-0.0.1-SNAPSHOT.jar ./user-recommendation-service.jar

# Set environment variables
ENV RATINGS_FILE_PATH=/app/ratings_final.txt
ENV USER_MAP_FILE_PATH=/app/user_mapping.json
ENV ITEM_MAP_FILE_PATH=/app/item_mapping.json
ENV KG_FILE_PATH=/app/kg_final.txt
ENV POST_MAP_FILE_PATH=/app/post_mapping.json

# Expose the application port
EXPOSE 8003

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "user-recommendation-service.jar"]
