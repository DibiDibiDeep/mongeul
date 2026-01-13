# Commit Message Convention

본 문서는 프로젝트의 커밋 메시지를 일관되게 관리하기 위한 규칙을 정의한다.  
본 규칙은 **Conventional Commits Specification v1.0.0**을 기반으로 하며,  
커밋 로그의 가독성 향상과 변경 이력 추적, 자동화 도구 연계를 목적으로 한다.

- 참고: [https://www.conventionalcommits.org/ko/v1.0.0/](https://www.conventionalcommits.org/ko/v1.0.0/)

#### 요약
- `feat` : 새로운 기능 추가 (또는 기존 기능의 의미 있는 확장)
- `fix` : 버그 수정 (또는 의도하지 않은 동작의 수정)
- `refactor` : 외부 동작 변화 없이 내부 코드 구조를 개선하는 변경
- `docs` : 문서 수정 또는 추가 (README, 주석, API 문서, 가이드 문서 등)
- `style` : 코드 동작에 영향을 주지 않는 스타일 변경 (포맷팅, 공백, 세미콜론, 린트 수정 등)
- `chore` : 기타 잡무성 작업 (환경 설정, 패키지 업데이트, 스크립트 정리 등)
- 간단 예시: `feat(auth): add JWT refresh token`


---

### 커밋 메시지 기본 구조

커밋 메시지는 다음 형식을 따른다.

```text
<타입>[적용 범위(선택 사항)]: <설명>

[본문(선택 사항)]

[꼬리말(선택 사항)]
```

#### 구조 설명

- 타입(Type): 변경의 성격을 나타내는 접두어
- 적용 범위(Scope): 변경이 영향을 주는 기능 또는 모듈 (선택)
- 설명(Description): 변경 내용을 간결한 명령문 형태로 작성
- 본문(Body): 변경 배경, 상세 설명 (선택)
- 꼬리말(Footer): Breaking Change, 이슈 번호 등 메타 정보 (선택)


#### 커밋 메세지 예시:
```
feat(chat): add streaming response support

기존 요청-응답 방식의 채팅 API를
SSE(Server-Sent Events) 기반 스트리밍 구조로 변경하였다.
이를 통해 응답 대기 시간을 줄이고 사용자 체감 속도를 개선했다.

BREAKING CHANGE: 기존 /api/chat 엔드포인트가
/api/chat/stream 로 변경되었으며,
클라이언트 측 구현 수정이 필요하다.
```

### 접두어 (Type / Prefix)

<b>접두어(prefix)</b>는 커밋의 **변경 성격을 한 단어로 명확히 표현**하기 위한 식별자이다.  
Conventional Commits 규칙에 따라 커밋 메시지의 가장 앞에 위치한다.

#### 주요 접두어
- `feat` : 새로운 기능 추가 (또는 기존 기능의 의미 있는 확장)
- `fix` : 버그 수정 (또는 의도하지 않은 동작의 수정)
- `refactor` : 외부 동작 변화 없이 내부 코드 구조를 개선하는 변경
- `docs` : 문서 수정 또는 추가 (README, 주석, API 문서, 가이드 문서 등)
- `test` : 테스트 코드 추가/수정/리팩토링 (단위 테스트, 통합 테스트 등)
- `style` : 코드 동작에 영향을 주지 않는 스타일 변경 (포맷팅, 공백, 세미콜론, 린트 수정 등)
- `chore` : 기타 잡무성 작업 (환경 설정, 패키지 업데이트, 스크립트 정리 등)
- `build` : 빌드 시스템 또는 런타임 의존성에 영향을 주는 변경 (예: Dockerfile, webpack, gradle, npm 설정 등)
- `ci` : CI/CD 파이프라인 및 자동화 설정 변경 (GitHub Actions, GitLab CI, Jenkins 설정 등)
- `perf` : 성능 개선을 위한 변경 (속도 최적화, 메모리 사용 감소 등)
- `revert` : 이전 커밋을 되돌리는 작업 (`git revert` 결과 커밋 포함)

#### 사용 예
```text
feat(chat): add streaming response
fix(api): handle null payload
chore(ci): update github actions
```

>접두어는 커밋의 의도를 빠르게 파악하게 해주며,
>자동 버전 관리, CHANGELOG 생성 등 다양한 자동화의 기반이 된다.

### 적용 범위 (Scope)

<b>적용 범위(scope)</b>는 해당 커밋이 **코드베이스의 어느 영역에 영향을 주는지**를 나타내는 식별자이다.  
파일명이 아닌 **기능, 도메인, 모듈 등 책임 단위**를 기준으로 작성한다.

#### 사용 예
- 기능/도메인: `auth`, `chat`, `search`, `agent`
- 레이어/기술: `api`, `service`, `db`, `infra`
- 모듈/컴포넌트: `parser`, `stream`, `scheduler`
- 환경/플랫폼: `docker`, `ci`, `aws`

```text
feat(auth): add JWT refresh token
fix(chat): handle empty message
build(docker): reduce image size
```

#### 작성 가이드
- 의미가 명확한 단일 책임 단위를 사용한다.
- utils, code, stuff처럼 포괄적인 단어는 피한다.
- 파일명이나 경로는 scope로 사용하지 않는다.
- 필요하지 않은 경우 생략해도 무방하다.

> Scope는 선택 사항이지만, 사용하면 커밋 로그 가독성과 변경 이력 추적성이 크게 향상된다.