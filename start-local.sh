#!/bin/bash

# 로컬 개발 환경 실행 스크립트 (SSL 없음)
# 사용법: ./start-local.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/infra"

echo "🚀 로컬 개발 환경 시작 중..."

# .env 파일 확인
if [ ! -f "../apps/backend/.env.dev" ] || [ ! -f "../apps/frontend/.env.dev" ] || [ ! -f "../apps/ml/.env.dev" ]; then
    echo "⚠️  경고: .env 파일이 없습니다."
    echo "   다음 파일들을 생성해주세요:"
    echo "   - apps/backend/.env.dev"
    echo "   - apps/frontend/.env.dev"
    echo "   - apps/ml/.env.dev"
    echo ""
    echo "   .env.example 파일을 참고하여 생성하세요."
fi

# Milvus 네트워크 확인
if ! docker network ls | grep -q milvus; then
    echo "📦 Milvus 네트워크가 없습니다. docker-compose-milvus.yml을 먼저 실행하세요."
    echo "   cd infra && docker compose -f docker-compose-milvus.yml up -d"
    exit 1
fi

# 기존 컨테이너 중지 및 제거
echo "🧹 기존 컨테이너 정리 중..."
docker compose -f docker-compose.dev.yml down

# 이미지 빌드 및 컨테이너 시작
echo "🔨 이미지 빌드 중..."
docker compose -f docker-compose.dev.yml build

echo "▶️  컨테이너 시작 중..."
docker compose -f docker-compose.dev.yml up -d

# 서비스 상태 확인
echo ""
echo "⏳ 서비스 시작 대기 중..."
sleep 5

echo ""
echo "✅ 서비스가 시작되었습니다!"
echo ""
echo "📍 접속 정보:"
echo "   - 프론트엔드: http://localhost:3000"
echo "   - 백엔드 API: http://localhost:8080"
echo "   - ML 서비스: http://localhost:8000"
echo "   - Redis: localhost:6389"
echo ""
echo "📋 로그 확인:"
echo "   cd infra && docker compose -f docker-compose.dev.yml logs -f"
echo ""
echo "🛑 중지:"
echo "   cd infra && docker compose -f docker-compose.dev.yml down"
