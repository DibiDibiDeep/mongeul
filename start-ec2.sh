#!/bin/bash

# EC2 프로덕션 환경 실행 스크립트 (SSL 발급 예정)
# 사용법: ./start-ec2.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/infra"

echo "🚀 EC2 프로덕션 환경 시작 중..."

# .env 파일 확인
if [ ! -f "../apps/backend/.env.prod" ] || [ ! -f "../apps/frontend/.env.prod" ] || [ ! -f "../apps/ml/.env.prod" ]; then
    echo "⚠️  경고: .env 파일이 없습니다."
    echo "   다음 파일들을 생성해주세요:"
    echo "   - apps/backend/.env.prod"
    echo "   - apps/frontend/.env.prod"
    echo "   - apps/ml/.env.prod"
    echo ""
    echo "   .env.example 파일을 참고하여 생성하세요."
fi

# Milvus 네트워크 확인
if ! docker network ls | grep -q milvus; then
    echo "📦 Milvus 네트워크가 없습니다. docker-compose-milvus.yml을 먼저 실행하세요."
    echo "   cd infra && docker compose -f docker-compose-milvus.yml up -d"
    exit 1
fi

# SSL 인증서 확인
SSL_CERT="./nginx/ssl/fullchain.pem"
SSL_KEY="./nginx/ssl/privkey.pem"

if [ ! -f "$SSL_CERT" ] || [ ! -f "$SSL_KEY" ]; then
    echo "⚠️  SSL 인증서가 없습니다!"
    echo ""
    echo "📝 SSL 인증서를 발급하려면:"
    echo ""
    echo "   1. Let's Encrypt 사용 (권장):"
    echo "      sudo apt-get update"
    echo "      sudo apt-get install certbot"
    echo "      sudo certbot certonly --webroot \\"
    echo "        -w infra/nginx/certbot \\"
    echo "        -d example.com \\"
    echo "        -d www.example.com"
    echo ""
    echo "      # 인증서 복사"
    echo "      sudo cp /etc/letsencrypt/live/example.com/fullchain.pem infra/nginx/ssl/"
    echo "      sudo cp /etc/letsencrypt/live/example.com/privkey.pem infra/nginx/ssl/"
    echo "      sudo chown \$USER:\$USER infra/nginx/ssl/*.pem"
    echo ""
    echo "   2. 자체 서명 인증서 (테스트용):"
    echo "      cd infra/nginx/ssl"
    echo "      openssl req -x509 -nodes -days 365 -newkey rsa:2048 \\"
    echo "        -keyout privkey.pem \\"
    echo "        -out fullchain.pem \\"
    echo "        -subj \"/CN=example.com\""
    echo ""
    echo "⚠️  SSL 인증서 없이 계속 진행하시겠습니까? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        exit 1
    fi
fi

# 기존 컨테이너 중지 및 제거
echo "🧹 기존 컨테이너 정리 중..."
docker compose -f docker-compose.prod.yml down

# 이미지 빌드 및 컨테이너 시작
echo "🔨 이미지 빌드 중..."
docker compose -f docker-compose.prod.yml build

echo "▶️  컨테이너 시작 중..."
docker compose -f docker-compose.prod.yml up -d

# nginx 설정 테스트
echo ""
echo "🔍 nginx 설정 확인 중..."
if docker compose -f docker-compose.prod.yml exec -T nginx nginx -t 2>&1 | grep -q "successful"; then
    echo "✅ nginx 설정이 올바릅니다."
else
    echo "❌ nginx 설정에 오류가 있습니다."
    docker compose -f docker-compose.prod.yml exec nginx nginx -t
    exit 1
fi

# 서비스 상태 확인
echo ""
echo "⏳ 서비스 시작 대기 중..."
sleep 5

echo ""
echo "✅ 서비스가 시작되었습니다!"
echo ""
echo "📍 접속 정보:"
echo "   - HTTPS: https://example.com"
echo "   - HTTP: http://example.com (HTTPS로 리다이렉트됨)"
echo ""
echo "📋 로그 확인:"
echo "   cd infra && docker compose -f docker-compose.prod.yml logs -f"
echo "   cd infra && docker compose -f docker-compose.prod.yml logs -f nginx  # nginx 로그만"
echo ""
echo "🛑 중지:"
echo "   cd infra && docker compose -f docker-compose.prod.yml down"
echo ""
echo "🔄 nginx 재시작:"
echo "   cd infra && docker compose -f docker-compose.prod.yml restart nginx"
