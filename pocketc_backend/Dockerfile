# ===== build stage =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

COPY gradlew ./gradlew
COPY gradle/wrapper/ gradle/wrapper/
COPY settings.gradle* build.gradle* gradle.properties* ./
RUN chmod +x gradlew 2>/dev/null || true && sed -i 's/\r$//' gradlew 2>/dev/null || true
RUN test -f gradle/wrapper/gradle-wrapper.jar || gradle -q wrapper --gradle-version 8.7

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENV TZ=Asia/Seoul
ENV SERVER_PORT=8080

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
