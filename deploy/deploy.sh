#!/bin/bash

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
  echo "❌ .env 파일이 없습니다. 환경 변수를 확인하세요."
  exit 1
fi

# 기존 컨테이너 중지 및 제거
docker-compose down

# 최신 이미지 강제 다운로드
docker-compose pull

# Nginx Proxy 먼저 실행 (필요한 네트워크 자동 생성됨)
docker-compose up -d nginx-proxy
echo "🟢 Nginx Proxy 실행 완료. 준비 대기 중..."
sleep 10  # Nginx 준비 시간 확보

# Let's Encrypt 컨테이너 실행 (인증서 발급)
docker-compose up -d letsencrypt
echo "🔒 SSL 인증서 발급 대기 중..."
sleep 5  # 인증서 검증 대기

# 애플리케이션 실행
docker-compose up -d
echo "🚀 애플리케이션 실행 완료"

# 인증서 존재 여부 확인
CERT_PATH="./nginx/certs/sunsu-wedding-backend.shop/fullchain.pem"
if [ ! -f "$CERT_PATH" ]; then
    echo "🚨 인증서가 존재하지 않습니다! letsencrypt 컨테이너 확인 필요"
    exit 1
fi

echo "✅ 배포 완료!"
