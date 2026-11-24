FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies || true

COPY src src
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
