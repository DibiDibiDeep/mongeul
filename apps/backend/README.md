# 몽글몽글 - Backend Repository

## 역할
|조은성 |정현수
|:-:|:-:|
|<img src='https://github.com/user-attachments/assets/f4f2a101-e705-4c67-826b-9591fe55259a' height=160 width=125></img>|<img src='https://github.com/user-attachments/assets/eadc750b-bd49-4abc-aa2b-78904b0c6fbd' height=160 width=125></img>|
|- backend api 개발 (캘린더, 챗봇, 알림장)<br>- ml서비스와 연동(캘린더, 챗봇, 알림장) <br> - SpringSecurity 구현|-backend api 개발(동화 구현(book, page))<br>- ml서비스와 연동(동화 구현) <br>- 문서화(api 명세서)

## API 디렉토리 구조
```
├── finalproj
│   ├── auth  # 구글Oauth, jwt관련 클래스
│   ├── config  # 설정 관련 클래스 (redis, restTemplate, S3, Scheduler, Security, Web)
│   ├── domain   # 도메인 api 클래스
│       ├── baby
│       ├── book
│       ├── calendar
│       ├── chat
│       ├── healthcheck
│       ├── memo
│       ├── notice
│       └── user
│   ├── ml # ml서비스 연동 관련 클래스
└── └── redis.service  #redis 연동 관련 클래스(챗봇 기능)
```
## 사용법

- root디렉토리에 `.env`파일 추가
```bash
ML_SERVICE_URL=
ML_APP_URL=

FRONT_URL=

# security
SPRING_SECURITY_ID=
SPRING_SECURITY_PWD=

# datasource
DB_USER=
DB_URL=
DB_PWD=
DRIVER_NAME=

# aws
AWS_ACCESS_KEY_ID=
AWS_SECRET_KEY=
AWS_REGION=
AWS_S3_BUCKET=

# jwt
JWT_SECRET=
JWT_EXPIRATION=

# google
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
GOOGLE_REDIRECT_URI=

# redis volume setting
REDIS_DATA_PATH=
REDIS_DEFAULT_CONFIG_FILE=

# redis etc setting
REDIS_BINDING_PORT=
REDIS_PORT=
REDIS_HOST=
REDIS_PASSWORD=
```

- docker image build
```bash
# build image
docker build -t [IMAGENAME]:[TAG] .

# run container
docker run --env-file .env --name [CONTAINERNAME] -p 8080:8080 [IMAGENAME]:[TAG] 
```


## 실행시 redis 설치

#### WSL 설치:

WSL이 설치되어 있지 않다면, PowerShell을 관리자 모드로 열고 다음 명령을 실행하세요:
wsl --install

#### Ubuntu 설치:
Microsoft Store에서 "Ubuntu"를 검색하여 Ubuntu를 설치합니다.

#### Redis 설치:

WSL(Ubuntu) 터미널을 열고 아래 명령을 실행하여 Redis를 설치합니다.
sudo apt update
sudo apt install redis-server

#### Redis 서버 실행:

Redis 설치 후, Redis 서버를 실행합니다.
sudo service redis-server start
#### Redis 확인:

Redis가 제대로 작동하는지 확인하려면 아래 명령어를 입력하세요:
redis-cli ping

결과가 PONG 이면 설치 완료

### macOS에 Redis 설치
macOS에서는 Homebrew를 통해 간단하게 Redis를 설치할 수 있습니다.
Homebrew 설치 (Homebrew가 설치되어 있지 않은 경우):
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

#### Redis 설치:
brew install redis

#### Redis 서버 실행:
brew services start redis

#### Redis 확인:
redis-cli ping

결과가 PONG이면 설치 완료

## api 명세서, ERD, 시스템/인프라 아키텍처 Wiki 참조
<a href="https://github.com/DibiDibiDeep/final-project/wiki">DIBIDIBIDEEP-Wiki</a>

