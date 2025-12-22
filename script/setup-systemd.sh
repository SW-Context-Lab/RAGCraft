#!/bin/bash

EC2_USER=ubuntu
EC2_HOST=ec2-52-79-194-231.ap-northeast-2.compute.amazonaws.com
KEY_PATH=/home/kim/Desktop/rag-craft.pem

SERVICE_NAME=ragcraft
REMOTE_APP_DIR=/home/ubuntu/ragcraft
SERVICE_FILE=/etc/systemd/system/${SERVICE_NAME}.service

ssh -i "$KEY_PATH" "$EC2_USER@$EC2_HOST" <<EOF

echo "▶ systemd 서비스 파일 생성"
sudo tee $SERVICE_FILE > /dev/null <<SERVICE_EOF
[Unit]
Description=ragcraft Spring Boot Application
After=network.target

[Service]
User=ubuntu
WorkingDirectory=$REMOTE_APP_DIR
EnvironmentFile=$REMOTE_APP_DIR/.env
ExecStart=/usr/bin/java -jar $REMOTE_APP_DIR/ragcraft-0.0.1-SNAPSHOT.jar
Restart=always
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
SERVICE_EOF

echo "▶ systemd 반영"
sudo systemctl daemon-reload
sudo systemctl enable $SERVICE_NAME
sudo systemctl restart $SERVICE_NAME

echo "▶ 서비스 상태"
sudo systemctl status $SERVICE_NAME --no-pager

EOF
