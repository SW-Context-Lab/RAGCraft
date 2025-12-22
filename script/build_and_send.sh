#!/bin/bash

# ===== 현재 스크립트 위치 =====
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ===== Spring Boot 프로젝트 루트 =====
PROJECT_ROOT="$SCRIPT_DIR/../ragcraft"

# ===== 서버 정보 =====
EC2_USER=ubuntu
EC2_HOST=ec2-52-79-194-231.ap-northeast-2.compute.amazonaws.com
KEY_PATH=/home/kim/Desktop/rag-craft.pem

REMOTE_DIR=/home/ubuntu/ragcraft
JAR_NAME=ragcraft-0.0.1-SNAPSHOT.jar
JAR_PATH="$PROJECT_ROOT/build/libs/$JAR_NAME"

echo "▶ SCRIPT_DIR     : $SCRIPT_DIR"
echo "▶ PROJECT_ROOT   : $PROJECT_ROOT"

echo "▶ 1. JAR 빌드"
cd "$PROJECT_ROOT"
"$PROJECT_ROOT/gradlew" clean build

echo "▶ 2. 서버 디렉터리 생성"
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_HOST" "mkdir -p $REMOTE_DIR"

echo "▶ 3. JAR 전송"
scp -i "$KEY_PATH" "$JAR_PATH" "$EC2_USER@$EC2_HOST:$REMOTE_DIR/"

echo "▶ 빌드 + JAR 전송 완료"

