FROM openjdk:17-jdk-alpine

# 환경변수 설정 (필요한 경우 추가)
ENV AWS_REGION=<AWS_REGION>
ENV AWS_ACCESS_KEY_ID=<AWS_ACCESS_KEY_ID>
ENV AWS_SECRET_ACCESS_KEY=<AWS_SECRET_ACCESS_KEY>

# AWS CLI 설치 및 설정 (필요한 경우 추가)
RUN apk update && apk add --no-cache py3-pip && \
    pip install awscli && \
    aws --version && \
    echo AWS_ACCESS_KEY_ID && \
    aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID && \
    aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY && \
    aws configure set default.region $AWS_REGION && \

# Spring Boot JAR 파일 복사 및 실행
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]




