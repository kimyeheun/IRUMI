# ===== build stage =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY settings.gradle* build.gradle* gradle.properties* ./
RUN gradle -q wrapper --gradle-version 8.7

COPY gradlew ./gradlew
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/

COPY src ./src
RUN chmod +x gradlew && ./gradlew --no-daemon clean bootJar -x test

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
