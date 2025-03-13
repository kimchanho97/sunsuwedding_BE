#!/bin/bash

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
  echo "❌ .env 파일이 없습니다. 환경 변수를 확인하세요."
  exit 1
fi

# 1. 기존 컨테이너 중지 및 정리
docker-compose down
docker volume prune -f
docker network prune -f

# 2. Nginx Proxy 먼저 실행
docker-compose up -d nginx-proxy
sleep 10  # Nginx 준비 시간 확보

# 3. Let's Encrypt 컨테이너 실행 (인증서 검증)
docker-compose up -d letsencrypt
sleep 5

# 4. 애플리케이션 실행
docker-compose up -d app redis

# 5. 인증서가 존재하는지 최종 확인
if [ ! -f "./nginx/certs/sunsu-wedding-backend.shop/fullchain.pem" ]; then
    echo "🚨 인증서가 존재하지 않습니다! letsencrypt 컨테이너 확인 필요"
    exit 1
fi

echo "✅ 배포 완료!"
