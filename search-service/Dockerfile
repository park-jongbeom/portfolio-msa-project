FROM openjdk:17-jdk-slim

WORKDIR /app

# [수정] 루트에서 실행할 것이므로 'search-service/' 경로가 포함되어야 합니다.
ARG JAR_FILE=search-service/build/libs/*.jar

# ARG 변수를 사용하여 복사
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]