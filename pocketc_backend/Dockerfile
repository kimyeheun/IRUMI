# ===== build stage =====
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

# 전체 먼저 복사
COPY . .

# gradlew 실행권한 & (윈도우 라인엔딩 방지) 정규화
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew

# 캐시 워밍업(선택)
RUN ./gradlew --no-daemon help || true

# 실제 빌드
RUN ./gradlew --no-daemon clean bootJar -x test

# ===== run stage =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENV TZ=Asia/Seoul
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /app/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
