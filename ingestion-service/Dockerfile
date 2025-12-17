# 1. Base Image (Java 17)
FROM amazoncorretto:17

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 위치 설정
# (명령어를 루트에서 실행할 것이므로, 경로에 모듈명이 포함되어야 합니다)
ARG JAR_FILE=ingestion-service/build/libs/*.jar

# 4. JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 5. 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]