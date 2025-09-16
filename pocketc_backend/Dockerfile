# ===== build stage =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY gradlew gradle/ ./
COPY settings.gradle* build.gradle* gradle.properties* ./
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew
RUN ./gradlew --no-daemon -v || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# ===== run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENV TZ=Asia/Seoul
ENV SERVER_PORT=8080

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
