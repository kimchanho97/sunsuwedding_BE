#!/bin/bash

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
  echo "❌ .env 파일이 없습니다. 환경 변수를 확인하세요."
  exit 1
fi

echo "🧼 기존 Docker 리소스 정리 중..."

# 컨테이너, 네트워크, 볼륨 중지 및 제거
docker-compose down -v --remove-orphans

# 사용되지 않는 이미지, 볼륨 등 정리
docker system prune -a --volumes -f

echo "✅ Docker 정리 완료"

# 최신 이미지 강제 pull
docker-compose pull

# 애플리케이션 실행
docker-compose up -d nginx-proxy app redis
echo "🚀 애플리케이션 실행 완료"

echo "✅ 배포 완료!"
