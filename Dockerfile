FROM eclipse-temurin:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 결과물 복사 (미리 빌드된 JAR 파일)
COPY build/libs/*.jar app.jar

# 컨테이너에서 실행될 포트
EXPOSE 8080

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]
