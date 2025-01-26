FROM openjdk:17-alpine

# Install Syft
RUN apk add --no-cache curl && \
    curl -sSfL https://raw.githubusercontent.com/anchore/syft/main/install.sh | sh

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/sbom-generator-1.0-SNAPSHOT.jar sbom-generator-1.0-SNAPSHOT.jar

# Set entry point
ENTRYPOINT ["java", "-jar", "sbom-generator-1.0-SNAPSHOT.jar"]
