#!/bin/bash

# .env 파일이 존재하는지 확인
if [ ! -f .env ]; then
  echo "❌ .env 파일이 없습니다. 환경 변수를 확인하세요."
  exit 1
fi

# 환경 변수 로드 (GitHub Secrets에서 주입)
export $(cat .env | xargs)

# 기존 컨테이너 중지 및 삭제
docker-compose -f ~/sunsuwedding-deploy/docker-compose.yml down

# 최신 이미지 가져오기
docker-compose -f ~/sunsuwedding-deploy/docker-compose.yml pull

# 컨테이너 실행
docker-compose -f ~/sunsuwedding-deploy/docker-compose.yml up -d

# 배포 후 컨테이너 실행 상태 확인
sleep 10

# 실행 중인 컨테이너 목록 출력 (확인용)
docker ps

echo "✅ Deployment successful!"
