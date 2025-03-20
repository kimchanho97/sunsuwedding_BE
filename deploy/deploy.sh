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

# 애플리케이션 실행
docker-compose up -d nginx-proxy app redis
echo "🚀 애플리케이션 실행 완료"

echo "✅ 배포 완료!"
