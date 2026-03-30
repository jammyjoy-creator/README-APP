# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven descriptor and download dependencies
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -q -e -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the build stage
# JAR name: <artifactId></artifact>-<version>.jar => demo-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# App will listen on 8080 inside the container
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]