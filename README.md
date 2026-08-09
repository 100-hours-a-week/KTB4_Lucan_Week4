# BallTalk Back-end

KBO 팬들이 팀별 게시판에서 게시글, 댓글, 좋아요로 소통하는 커뮤니티 프로젝트입니다.

## 프로젝트 정보

- 프로젝트명: BallTalk
- 한 줄 설명: 팀별 게시판을 제공하는 KBO 팬 커뮤니티
- 개발 기간: 2026-05-26 ~ 2026-08-09
- Front-end GitHub Repository: https://github.com/SeoSeungMin1/KTB4_Lucan_Week10
- 서비스 시연 영상: https://drive.google.com/file/d/1TlciT7dM1rqPxILdedFm15zswWAjGuwc/view?usp=sharing



## 사용 기술 및 Tools

| 구분 | 기술 |
| --- | --- |
| Language | Java |
| Framework | Spring Boot |
| Build Tool | Gradle |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security, BCrypt |
| Database | MySQL, H2 |
| Storage/CDN | Amazon S3, CloudFront |
| Container | Docker |


## 폴더 및 패키지 구조

<details>
<summary>폴더 구조 보기/숨기기</summary>
  
```text
community/
├── src/
│   ├── main/
│   │   ├── java/com/lucan/community/
│   │   │   ├── CommunityApplication.java
│   │   │   ├── config/
│   │   │   │   ├── S3Config.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── UserController.java
│   │   │   │   ├── PostController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   └── S3Controller.java
│   │   │   ├── dto/
│   │   │   │   ├── user/
│   │   │   │   ├── post/
│   │   │   │   ├── comment/
│   │   │   │   ├── like/
│   │   │   │   └── response/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── message/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-local.properties
│   │       └── application-prod.properties
│   └── test/
├── k6-tests/
├── build.gradle
├── Dockerfile
└── README.md
```

</details>

## 서버 설계

서버는 역할에 따라 Controller, Service, Repository, Entity 계층으로 구성되어 있습니다.

### Controller

HTTP 요청을 받고 DTO 검증과 응답 생성을 담당

| Controller | 책임 |
| --- | --- |
| `UserController` | 회원가입, 내 정보 조회·수정, 비밀번호 변경, 회원 탈퇴 |
| `PostController` | 게시글 목록·상세·작성·수정·삭제, 최신글·인기글, 조회수, 좋아요 |
| `CommentController` | 게시글 댓글 조회·작성·수정·삭제 |
| `S3Controller` | multipart 이미지 업로드 |

### Service

트랜잭션과 비즈니스 규칙을 담당

- `UserService`: 중복 검사, 비밀번호 암호화, 회원정보 변경, 회원 탈퇴
- `PostService`: 게시글 CRUD, 목록 집계, 인기글·최신글, 조회수, 좋아요
- `CommentService`: 댓글 CRUD와 작성자 권한 검증
- `S3Service`: 이미지 검증, S3 업로드·삭제, CloudFront URL 생성

조회 메서드는 `readOnly = true`, 변경 메서드는 일반 트랜잭션으로 실행

### Repository

Spring Data JPA의 `JpaRepository`를 사용

- 게시글 목록은 JPQL 생성자 표현식으로 화면에 필요한 DTO를 직접 조회

### Entity

| Entity | 테이블 | 역할 |
| --- | --- | --- |
| `User` | `users` | 계정, 응원팀, 프로필, 탈퇴 여부 |
| `Post` | `posts` | 팀 게시판 게시글과 조회수 |
| `Comment` | `comments` | 게시글 댓글 |
| `PostLike` | `post_likes` | 사용자별 게시글 좋아요 |
| `PostImage` | `post_images` | 게시글 이미지 URL |

모든 연관관계는 자식 Entity에서 부모 Entity를 참조하는 단방향 `ManyToOne`이며 LAZY 로딩 사용

## 주요 기능

### Users

- 이메일 형식, 비밀번호 길이, 닉네임 길이 검증
- 이메일과 닉네임 중복 방지
- BCrypt 비밀번호 암호화
- 응원팀 선택과 선택적으로 프로필 이미지 포함 회원가입
- 로그인 사용자 정보 조회
- 프로필 이미지가 있으면 S3 객체 삭제 후 회원 탈퇴
- 회원 레코드를 삭제하지 않고 `deleted = true`로 변경하는 soft delete
- 탈퇴 시 닉네임을 `탈퇴 유저`로 변경하고 HTTP 세션 무효화

### Posts

- 전체 또는 팀별 게시글 목록 조회
- `page`, `size` 기반 페이지네이션
- 작성일 내림차순 정렬
- 목록에서 좋아요 수, 댓글 수, 조회수 제공
- 응원팀 최신 게시글 3개 제공
- 응원팀 인기 게시글 3개 제공 (인기글 정렬 기준은 좋아요 수, 조회수, 작성일의 내림차순)
- 작성자만 제목, 내용, 이미지를 수정 가능
- 작성자만 게시글과 연관 댓글·좋아요·이미지를 삭제 가능

### Comments

- 게시글별 댓글을 최신 작성 순으로 조회
- 빈 댓글 작성·수정 방지
- 댓글 작성자만 수정·삭제 가능

### Likes

- 게시글 좋아요와 좋아요 취소
- 좋아요 처리 후 현재 좋아요 수 반환
- Repository 검사와 DB 유니크 제약으로 중복 좋아요 방지

### Images

- 프로필 이미지와 게시글 이미지 업로드
- UUID 기반 S3 object key 생성
- 이미지 교체 시 기존 S3 객체 삭제
- 게시글 삭제와 회원 탈퇴 시 관련 이미지 삭제
- 최대 파일 및 요청 크기 10MB

### Team 관련 기능

지원하는 팀 enum 10개

```text
LG, KT, SSG, NC, KIA, SAMSUNG, LOTTE, DOOSAN, HANWHA, KIWOOM
```

- 회원가입 시 응원팀 선택
- 게시글 작성 시 팀 게시판 선택
- 특정 팀의 게시글 목록 필터링
- 로그인 사용자의 응원팀을 기준으로 최신글·인기글 제공
- 작성자와 댓글 작성자의 응원팀 정보 제공

## Spring Security 인증 및 인가

### 인증 흐름

- 사용자가 `POST /users/login`으로 이메일과 비밀번호를 전송
- Spring Security의 `formLogin`이 로그인 요청을 처리
- `CustomUserDetailsService.loadUserByUsername(email)`을 호출하여 사용자 정보 조회
- `UserRepository.findByEmail(email)`을 통해 DB에서 사용자 조회
- BCrypt를 통해 입력한 비밀번호와 저장된 암호화 비밀번호 검증
- 인증 성공 시 `SecurityContext`에 인증 정보 저장
- `SecurityContext`를 HTTP Session에 저장
- 이후 클라이언트는 `JSESSIONID` 쿠키를 통해 로그인 상태 유지

### 인가
- 인증이 필요한 요청은 로그인 사용자만 접근
- 게시글 수정/삭제 -> 게시글 작성자인지 확인
- 댓글 수정/삭제 -> 댓글 작성자인지 확인
- Service에서 사용자 ID와 작성자 ID 비교

### 공개 경로
- `/users/signup`
- `/users/login`
- `/h2-console/**`
- `/css/**`
- `/Js/**`
- `/images/**`
- `GET /posts`

## 데이터베이스 설계

### Entity 관계

```text
User 1 ── N Post
User 1 ── N Comment
User 1 ── N PostLike
Post 1 ── N Comment
Post 1 ── N PostLike
Post 1 ── N PostImage
```

### 테이블 요약

| 테이블 | 주요 컬럼 및 제약 |
| --- | --- |
| `users` | `user_id` PK, `email` UNIQUE, `nickname` UNIQUE, `favorite_team` NOT NULL, `deleted` |
| `posts` | `post_id` PK, `user_id` FK, `title`, `content` TEXT, `team` NOT NULL, `view_count` |
| `comments` | `comment_id` PK, `user_id` FK, `post_id` FK, `content` TEXT |
| `post_likes` | `like_id` PK, `user_id` FK, `post_id` FK, `(user_id, post_id)` UNIQUE |
| `post_images` | `post_image_id` PK, `post_id` FK, `image` |

각 Entity는 `createdAt`을 생성 시 설정하며, `User`, `Post`, `Comment`, `PostImage`는 수정 시각도 관리합니다.

### ERD

<img width="1181" height="603" alt="image" src="https://github.com/user-attachments/assets/a968894f-6685-45da-b029-0c8b880c031d" />

## 인덱스 및 조회 성능 관련 구현

### DB 인덱스

| 테이블 | 인덱스 |
| --- | --- |
| `posts` | `created_at` |
| `posts` | `(team, created_at)` |
| `comments` | `(post_id, created_at)` |
| `post_likes` | `post_id` |
| `post_likes` | `(user_id, post_id)` UNIQUE |

### 조회 구현

- 전체·팀별 게시글은 `Pageable`을 사용해 DB에서 페이지 단위로 조회
- 게시글 목록은 JPQL DTO projection을 사용
- 좋아요와 댓글을 LEFT JOIN하고 `COUNT(DISTINCT ...)`로 중복 집계를 방지
- 최신글과 인기글은 `PageRequest.of(0, 3)`으로 최대 3개만 조회

## AWS S3 / CloudFront 이미지 처리

- 프로필 및 게시글 이미지를 AWS S3에 저장
- 이미지 파일은 UUID 기반의 고유한 이름으로 관리
- CloudFront를 통해 이미지 URL 제공
- 이미지 교체 및 삭제 시 S3의 기존 이미지도 함께 삭제
- DB에는 이미지 파일이 아닌 이미지 URL을 저장

## Docker 및 배포

- Front-end와 Back-end에 멀티 스테이지 Dockerfile 적용
- Docker Compose를 통해 Front-end와 Back-end 컨테이너 통합 관리
- Back-end 컨테이너는 `8080` 포트 사용
- 환경변수는 `.env`를 통해 관리
- 로컬 환경은 H2, 운영 환경은 MySQL 사용

## 트러블 슈팅

### 1. Spring Security 도입 과정에서 인증 구조 개선

기존에는 Front-end의 `localStorage`에 저장된 `userId`를 이용해 사용자를 식별했습니다.  
Spring Security를 적용하면서 세션 기반 인증을 도입했지만, 기존 `userId` 방식과 세션 인증 방식이 함께 사용되면서 인증 정보 관리가 이중화되고 일부 API 요청에서 `401 Unauthorized`가 발생하는 문제가 있었습니다.
이를 해결하기 위해 Spring Security의 인증 정보를 기준으로 로그인 사용자를 식별하도록 변경하고, `@AuthenticationPrincipal`을 통해 서버에서 로그인 사용자 정보를 가져오도록 수정했습니다. Front-end에서는 `credentials: include`를 적용해 `JSESSIONID` 쿠키가 요청에 포함되도록 구성했습니다.
이를 통해 사용자 식별을 서버의 세션 인증 방식으로 통일하고, 인증과 인가의 역할을 구분하여 관리할 수 있었습니다.

### 2. 게시글 목록 조회 시 N+1 문제 개선

게시글 목록에서는 게시글 정보뿐만 아니라 작성자 정보, 좋아요 수, 댓글 수가 함께 필요했습니다.
연관관계를 LAZY Loading으로 설정한 상태에서 게시글을 먼저 조회한 뒤 각 게시글의 연관 데이터를 개별적으로 조회할 경우, 게시글 목록 조회 쿼리 1번 이후 게시글 수만큼 추가 쿼리가 발생하는 N+1 문제가 발생할 수 있었습니다.
이를 해결하기 위해 JPQL의 `JOIN`과 DTO Projection을 적용하여 게시글 목록에 필요한 게시글 정보, 작성자 정보, 좋아요 수, 댓글 수를 `PostListResponse` DTO로 한 번에 조회하도록 구성했습니다.

또한 좋아요와 댓글을 동시에 JOIN하면서 발생할 수 있는 중복 집계를 방지하기 위해 `COUNT(DISTINCT ...)`를 사용했습니다.

### 3. 조회 패턴을 고려한 인덱스 적용 및 성능 확인

게시글과 댓글은 등록이나 수정이나 삭제에 비해 조회가 빈번하게 발생하며, 전체 게시글 최신순 조회, 팀별 최신순 조회, 게시글별 댓글 조회처럼 반복적으로 사용되는 조회 패턴이 존재했습니다.
이에 실제 조회 조건과 정렬 기준을 고려하여 인덱스를 적용했고,
단순히 개별 컬럼에 인덱스를 추가하는 것이 아니라 `WHERE` 조건과 `ORDER BY`에 함께 사용되는 컬럼을 고려해 복합 인덱스를 적용했습니다.
이후 약 1만 건의 테스트 데이터를 생성하고 부하 테스트를 진행하여 게시글 및 댓글 목록 조회가 정상적으로 처리되는지 확인하고, 인덱스 적용 후 조회 성능을 확인했습니다.

## 프로젝트 후기

이번 프로젝트를 진행하면서 Spring Boot와 JPA를 이용한 기본적인 CRUD 구현에서 시작해 Spring Security 기반의 인증·인가, 조회 성능 개선, AWS 배포까지 Back-end 개발의 전체적인 흐름을 경험할 수 있었습니다.
특히 게시글 목록 조회 과정에서 발생할 수 있는 N+1 문제를 고려해 JOIN과 DTO Projection을 적용하고, 실제 조회 패턴에 맞춰 단일 및 복합 인덱스를 설계했습니다. 또한 1만 건의 테스트 데이터를 활용한 부하 테스트를 진행하면서 단순히 기능이 동작하는 것뿐만 아니라 데이터가 증가했을 때의 성능까지 고려해 볼 수 있었습니다.
가장 아쉬웠던 점은 프로젝트 후반에 배포에서 어려움을 겪어 많은 시간을 사용하면서 서비스 자체의 기능 구현과 고도화에는 충분한 시간을 투자하지 못했다는 점입니다.
또한 프로젝트를 진행하면서 초반에 기획이 중요하다는 것을 다시 생각하게 되었습니다. 처음부터 실제 사용자가 어떤 이유로 이 서비스를 사용할지, 기존 서비스와 어떤 차별점이 있는지를 더 깊게 고민했다면 기능의 방향도 더욱 명확하게 잡을 수 있었을 것이라는 아쉬움이 남았습니다.
다음 프로젝트에서는 개발 초기부터 핵심 기능과 사용자 경험을 구체적으로 정의하고, 기능 구현에 충분한 시간을 확보한 뒤 성능 개선과 배포까지 단계적으로 진행하고 싶습니다.
