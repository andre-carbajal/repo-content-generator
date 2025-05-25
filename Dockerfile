# syntax=docker/dockerfile:1

# ----------- Build Stage -----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY --link pom.xml mvnw ./
COPY --link .mvn .mvn/

# Make sure the Maven wrapper is executable
RUN chmod +x mvnw

# Download dependencies (leverages Docker cache)
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY --link src ./src
COPY --link native-image-config ./native-image-config

# Build the application (skip tests for faster build)
RUN ./mvnw package -DskipTests

# ----------- Runtime Stage -----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Set permissions
RUN chown -R appuser:appgroup /app
USER appuser

# Expose the default Spring Boot port
EXPOSE 8080

# JVM options for container awareness and memory limits
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
