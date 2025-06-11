#!/bin/bash

cd "$(dirname "$0")"

echo "[DEPLOY] backend 배포 시작"

echo "[DEPLOY] 컨테이너 중단 및 삭제"
docker compose -f docker-compose-back.yaml down || true
docker rm -f back-blue || true

echo "[DEPLOY] 이미지 제거"
docker rmi -f weatherdecenhub.store/itcen-back-blue:latest || true

sleep 5

echo "[DEPLOY] docker compose 실행"
docker compose -f docker-compose-back.yaml up -d

echo "[DEPLOY] 완료"
