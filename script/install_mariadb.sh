#!/bin/bash

# EC2 서버에 SSH로 접속
ssh -i "/home/kim/Desktop/rag-craft.pem" ubuntu@ec2-52-79-194-231.ap-northeast-2.compute.amazonaws.com <<'EOF'

echo 'MariaDB 설치시작'
sudo apt update -y
sudo apt upgrade -y
curl -LsS https://r.mariadb.com/downloads/mariadb_repo_setup | sudo bash -s -- \
  --mariadb-server-version="mariadb-10.11" --os-type="ubuntu" --os-version="jammy"


echo 'MariaDB 서버, 클라이언트 설치'
sudo apt install mariadb-server -y
sudo apt install mariadb-client -y

echo 'MariaDB 서비스 시작 및 부팅 시 자동 시작 설정'
sudo systemctl start mariadb
sudo systemctl enable mariadb

echo 'ragcraft 데이터베이스 생성'
sudo mysql <<DB_EOF
CREATE DATABASE IF NOT EXISTS ragcraft
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'ragcraft'@'localhost' IDENTIFIED BY 'ragcraft';
GRANT ALL PRIVILEGES ON ragcraft.* TO 'ragcraft'@'localhost';
FLUSH PRIVILEGES;
SHOW DATABASES;
DB_EOF


echo 'MariaDB 확인 및 버전 출력'
echo "MariaDB 설치 및 설정 완료:"
mysql --version


EOF
