# 1️⃣ Gradle 빌드 환경 설정 (빌드 전용)
FROM gradle:8.4-jdk21 AS builder  # Java 21로 변경

# 2️⃣ 작업 디렉토리 설정
WORKDIR /app

# 3️⃣ 프로젝트 소스 코드 복사
COPY --chown=gradle:gradle . .

# 4️⃣ Gradle 빌드 실행 (테스트 제외, 빌드 최적화)
RUN ./gradlew build -x test --stacktrace  # --stacktrace 추가하여 상세 오류 확인

# 5️⃣ 실행 환경 설정 (Slim JDK)
FROM openjdk:21-jdk-slim  # Java 21로 변경

# 6️⃣ 작업 디렉토리 설정
WORKDIR /app

# 7️⃣ 빌드된 실행 가능한 JAR 파일만 복사 (Plain JAR 제외)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# 8️⃣ 컨테이너 실행 시 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
