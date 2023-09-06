FROM openjdk:17-jdk-alpine

# AWS CLI 설치 및 설정 (필요한 경우 추가)
RUN apk update && apk add --no-cache py3-pip && \
    pip install awscli && \
    aws --version

# Spring Boot JAR 파일 복사 및 실행
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]

