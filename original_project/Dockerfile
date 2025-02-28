# Base image: OpenJDK 17
FROM amazoncorretto:17-alpine-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container at /app
COPY build/libs/PayMilli-0.0.1-SNAPSHOT.jar /app/app.jar
COPY .env /app/

ENV TZ="Asia/Seoul"

# Expose port 8080 to the outside world
EXPOSE 8080

# Command to run the Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
