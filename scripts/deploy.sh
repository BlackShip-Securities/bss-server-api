#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/bss
cd $REPOSITORY

JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 "$CURRENT_PID"

  while kill -0 "$CURRENT_PID" 2> /dev/null; do
    echo "> 기존 프로세스 종료 대기 중..."
    sleep 1
  done

  echo "> 기존 프로세스 종료 완료!"
fi

# 로그 파일 설정 (날짜별 로그 파일 저장)
TIMESTAMP=$(date "+%Y-%m-%d_%H-%M-%S")
LOG_FILE="/home/ubuntu/bss/logs/app_$TIMESTAMP.log"

echo "> Deploy - $JAR_PATH"
nohup java -Dspring.profiles.active=prod -jar "$JAR_PATH" > "$LOG_FILE" 2>&1 &