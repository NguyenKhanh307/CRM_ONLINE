# syntax=docker/dockerfile:1

# --- build stage: build fat jar bằng Maven wrapper ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Cache dependency: copy wrapper + pom trước, tải offline
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copy source và build (bỏ test cho nhanh)
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# --- run stage: JRE 21 + font Unicode cho PDF tiếng Việt ---
FROM eclipse-temurin:21-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/be-crm-0.0.1-SNAPSHOT.jar app.jar

# Render inject biến PORT; app đọc qua server.port=${PORT:8080}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
