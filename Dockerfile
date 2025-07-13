# Use OpenJDK 21 as base image
FROM eclipse-temurin:21-jdk-ubi9-minimal AS build

RUN microdnf install -y git ca-certificates \
    && microdnf clean all

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src
COPY .git .

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-ubi9-minimal

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/grid.jar app.jar
COPY --from=build /app/src/main/webapp ./src/main/webapp

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose port 8080
EXPOSE 8080

ENV JAVA_OPTS=-Dggo.properties=/etc/ggo.properties

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
