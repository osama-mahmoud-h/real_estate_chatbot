# Stage 1: Build application
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy POM first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer - only re-runs if pom.xml changes)
RUN mvn dependency:resolve dependency:resolve-plugins -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/swagger-ui.html || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]