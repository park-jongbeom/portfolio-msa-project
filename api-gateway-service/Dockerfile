# 1. Base Image (AWS Corretto 사용)
FROM amazoncorretto:17

# 2. 작업 디렉토리
WORKDIR /app

# 3. JAR 파일 복사 (루트에서 빌드 명령어를 실행하므로 모듈 경로 포함)
ARG JAR_FILE=api-gateway-service/build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 실행 (환경변수는 docker run 시점에 주입됨)
ENTRYPOINT ["java", "-jar", "app.jar"]