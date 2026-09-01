# ================================
# Stage 1: Build the application
# ================================

FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# ================================
# Stage 2: Run the application
# ================================

FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]