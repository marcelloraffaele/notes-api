# Use openjdk:21-jdk-alpine as the base image
FROM eclipse-temurin:21-jre-ubi9-minimal

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the target directory to the Docker image
COPY target/note-api-1.0.0-SNAPSHOT.jar app.jar

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
