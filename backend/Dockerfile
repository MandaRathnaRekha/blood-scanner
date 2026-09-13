# ===================================================================
# LifePulse - Emergency Blood Alert System
# File: backend/Dockerfile
# Multi-stage production container build for Java 21 LTS Spring Boot
# ===================================================================

# -------------------------------------------------------------------
# Stage 1: Build Stage
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and dependencies specification
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Grant execution rights on wrapper script
RUN chmod +x mvnw

# Download dependencies (leveraging Docker layer cache)
RUN ./mvnw dependency:go-offline -B

# Copy project source code
COPY src/ ./src/

# Package executable production JAR skipping tests (tests verified locally/in CI)
RUN ./mvnw clean package -DskipTests -B

# -------------------------------------------------------------------
# Stage 2: Minimal Production Runtime
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Security: run application as a non-privileged user
RUN addgroup -S bloodbond && adduser -S bloodbond -G bloodbond
USER bloodbond:bloodbond

# Copy fat JAR from build stage
COPY --from=builder /app/target/blood-alert-system-*.jar app.jar

# Expose default HTTP port (overridden by $PORT in cloud environments)
EXPOSE 8080

# Configure JVM flags optimized for containerized memory
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Healthcheck probe against the new health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${PORT:-8080}/api/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
