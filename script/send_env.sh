#!/bin/bash

EC2_USER=ubuntu
EC2_HOST=ec2-52-79-194-231.ap-northeast-2.compute.amazonaws.com
KEY_PATH=/home/kim/Desktop/rag-craft.pem

LOCAL_ENV_PATH=/home/kim/Desktop/RAGCraft/ragcraft/.env
REMOTE_DIR=/home/ubuntu/ragcraft

echo "▶ 서버 디렉터리 생성"
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_HOST" "mkdir -p $REMOTE_DIR"

echo "▶ env 파일 전송"
scp -i "$KEY_PATH" "$LOCAL_ENV_PATH" "$EC2_USER@$EC2_HOST:$REMOTE_DIR/.env"

echo "▶ 서버 권한 설정"
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_HOST" "chmod 600 $REMOTE_DIR/.env"

echo "▶ env 전송 완료"
