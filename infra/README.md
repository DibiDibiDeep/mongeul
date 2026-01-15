# Infrastructure 설정 가이드

이 디렉토리는 개발(dev)과 프로덕션(prod) 환경을 분리하여 관리합니다.

## 디렉토리 구조

```
infra/
├── docker-compose.dev.yml      # 개발 환경 compose
├── docker-compose.prod.yml     # 프로덕션 환경 compose
├── docker-compose-milvus.yml   # Milvus 공통 설정
├── nginx/
│   ├── nginx.conf              # 공통 nginx 설정
│   ├── conf.d/
│   │   ├── dev.conf            # 개발용 (HTTP only)
│   │   └── prod.conf           # 프로덕션용 (HTTPS)
│   ├── ssl/                    # SSL 인증서 디렉토리
│   └── certbot/                # Let's Encrypt 인증용
└── redis.config.template       # Redis 공통 설정

apps/
├── backend/
│   ├── .env.dev                # 개발용 환경변수
│   ├── .env.prod               # 프로덕션용 환경변수
│   └── .env.example            # 환경변수 예시
├── frontend/
│   ├── .env.dev
│   ├── .env.prod
│   └── .env.example
└── ml/
    ├── .env.dev
    ├── .env.prod
    └── .env.example
```

## 개발 환경 (dev)

### 특징
- 포트 직접 노출 (디버깅 용이)
- SSL 없음
- nginx 사용 (HTTP only)
- 개발용 환경변수

### 실행 방법

```bash
# 프로젝트 루트에서 실행
./start-local.sh
```

또는 직접:

```bash
cd infra
docker compose -f docker-compose.dev.yml up -d
```

### 접속 정보
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8080
- ML 서비스: http://localhost:8000
- Redis: localhost:6389

## 프로덕션 환경 (prod)

### 특징
- nginx 필수 (리버스 프록시)
- SSL/TLS 필수
- 포트는 nginx만 노출
- 프로덕션 환경변수
- 리소스 제한 설정

### 실행 방법

```bash
# 프로젝트 루트에서 실행
./start-ec2.sh
```

### SSL 인증서 발급

#### 1. Let's Encrypt 사용 (권장)

```bash
# certbot 설치
sudo apt-get update
sudo apt-get install certbot

# 인증서 발급
sudo certbot certonly --webroot \
  -w infra/nginx/certbot \
  -d example.com \
  -d www.example.com

# 인증서 복사
sudo cp /etc/letsencrypt/live/example.com/fullchain.pem infra/nginx/ssl/
sudo cp /etc/letsencrypt/live/example.com/privkey.pem infra/nginx/ssl/
sudo chown $USER:$USER infra/nginx/ssl/*.pem
```

#### 2. 자체 서명 인증서 (테스트용)

```bash
cd infra/nginx/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout privkey.pem \
  -out fullchain.pem \
  -subj "/CN=example.com"
```

### 접속 정보
- HTTPS: https://example.com
- HTTP: http://example.com (HTTPS로 자동 리다이렉트)

## 환경 변수 파일

각 서비스별로 `.env.dev`와 `.env.prod` 파일을 생성해야 합니다:

### Backend (.env.dev, .env.prod)
```bash
# apps/backend/.env.example을 참고하여 생성
SPRING_SECURITY_ID=admin
SPRING_SECURITY_PWD=your_password
DB_URL=jdbc:mysql://localhost:3306/mydb
DB_USER=root
DB_PWD=your_password
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
# ... 기타 환경변수
```

### Frontend (.env.dev, .env.prod)
```bash
# apps/frontend/.env.example을 참고하여 생성
NEXT_PUBLIC_API_URL=http://localhost:8080
NODE_ENV=development
```

### ML (.env.dev, .env.prod)
```bash
# apps/ml/.env.example을 참고하여 생성
OPENAI_API_KEY=your_openai_key
MILVUS_HOST=milvus-standalone
MILVUS_PORT=19530
EMBEDDING_MODEL=text-embedding-3-small
EMBEDDING_DIMENSION=1536
COLLECTION_NAME=your_collection_name
AWS_ACCESS_KEY_ID=your_aws_key
AWS_SECRET_ACCESS_KEY=your_aws_secret
AWS_REGION=ap-northeast-2
AWS_S3_BUCKET=your_bucket_name
```

## 공통 설정

### Milvus 네트워크

dev/prod 모두 사용하는 Milvus 네트워크는 별도로 실행해야 합니다:

```bash
cd infra
docker compose -f docker-compose-milvus.yml up -d
```

## 프록시 라우팅

nginx를 사용할 때:

| 경로 | 대상 서비스 | 포트 |
|------|-------------|------|
| `/` | frontend (Next.js) | 3000 |
| `/api/*` | backend (Spring Boot) | 8080 |
| `/ml/*` | ml (FastAPI) | 8000 |

## 유용한 명령어

### 로그 확인
```bash
# 개발 환경
cd infra
docker compose -f docker-compose.dev.yml logs -f

# 프로덕션 환경
docker compose -f docker-compose.prod.yml logs -f

# 특정 서비스
docker compose -f docker-compose.dev.yml logs -f nginx
docker compose -f docker-compose.dev.yml logs -f backend
```

### 서비스 재시작
```bash
cd infra
docker compose -f docker-compose.dev.yml restart
docker compose -f docker-compose.dev.yml restart nginx
```

### 서비스 중지
```bash
cd infra
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.prod.yml down
```

### nginx 설정 테스트
```bash
cd infra
docker compose -f docker-compose.prod.yml exec nginx nginx -t
```

## 문제 해결

### 포트 충돌
포트가 이미 사용 중인 경우:
```bash
# 사용 중인 포트 확인
lsof -i :80
lsof -i :443

# docker-compose.yml에서 포트 변경
```

### SSL 인증서 오류
- 인증서 파일 경로 확인
- 파일 권한 확인 (`chmod 644 *.pem`)
- nginx 설정 파일의 경로 확인

### 네트워크 오류
Milvus 네트워크가 없는 경우:
```bash
docker network create milvus
```

## 주의사항

⚠️ **보안**
- SSL 인증서 파일은 절대 Git에 커밋하지 마세요
- 프로덕션 환경변수는 안전하게 관리하세요
- 기본 포트를 변경하는 것을 권장합니다

⚠️ **환경 분리**
- dev와 prod 설정을 혼동하지 마세요
- 프로덕션 설정을 개발 환경에서 사용하지 마세요
- 각 서비스의 `.env.dev`와 `.env.prod`를 올바르게 설정하세요