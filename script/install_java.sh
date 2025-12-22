#!/bin/bash

# EC2 서버에 SSH로 접속
ssh -i "/home/kim/Desktop/rag-craft.pem" ubuntu@ec2-52-79-194-231.ap-northeast-2.compute.amazonaws.com <<'EOF'

# Java 17 설치 스크립트

# 1. 패키지 업데이트
sudo apt update -y
sudo apt upgrade -y

# 2. 필요 패키지 설치
sudo apt install -y wget software-properties-common apt-transport-https

# 3. OpenJDK 17 설치
sudo apt install -y openjdk-17-jdk

# 4. Java 버전 확인
java -version

# 5. JAVA_HOME 설정
JAVA_HOME_PATH=$(dirname $(dirname $(readlink -f $(which java))))

if ! grep -q "JAVA_HOME" /etc/environment; then
  echo "JAVA_HOME=$JAVA_HOME_PATH" | sudo tee -a /etc/environment
fi

source /etc/environment

echo "Java 17 설치 완료!"

echo "JAVA_HOME=$JAVA_HOME"




EOF
