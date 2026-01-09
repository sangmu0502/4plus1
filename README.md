# 📕 ReadMe

# 🔉 Sparta Music API

---

목차

1. 팀원 소개 - 이름/역할
2. 프로젝트 개요 - 노션 + ppt 내용
3. 주요 기술 스택 - ppt
4. 데이터셋 추가 과정 <- 진수님만 작성 가능할 것으로 보임 (브랜치명 : search-tjs)
5. 실행 환경 - Redis: 도커에서 실행한 이후여야 한다/Indexing: 메인 패키지에 sql파일로 프로그램 실행과 동시에 진행되므로, 따로 필요한 설정은 없다
6. ERD - 첨부
7. 주요 기능
8. API 명세 - 캡쳐한 이미지 + 노션 링크
9. 트러블 슈팅 & 최적화 전략 - ppt내용 참고 / Redis랑 Indexing 각각 설명 첨부
10. 마무리 - 그냥 아래처럼 작성

## :bust_in_silhouette: 개발자

---

Spring plus 팀 프로젝트 4조 4+1
이상무, 김동욱, 임정하, 장서연, 탁진수
각자 git 하이퍼링크 달면 좋음

---

redis & k6 부하테스트

플레이리스트 노래 목록 조회 api

- PlaylistSong + Song join + SongArtist 추가 조회
- 첫 페이지( 0 page )가 가장 많이 조회
- 플레이리스트 첫 페이지는 조회 빈도가 높고 변경 빈도가 낮음 → Cache 사용에 유리

k6 부하테스트

- 50 VUs / 30s
- 레디스 사용 전후 부하테스트 결과 캡처 넣기

---

>

## 1. 팀원 및 역할 소개

| 🚩이상무 | 김동욱 | 임정하 | 장서연 | 탁진수 |
| --- | --- | --- | --- | --- |
| - 노래 API
- 좋아요 API | - 앨범 API
- 장르 노래 API | - 인증 / 인가
- 유저 API | - 플레이리스트 API | - 검색 API |

## 2. 프로젝트 개요

본 프로젝트는 멜론에서 제공하는 약 70만 건의 음악 메타데이터셋을 가공하여 구축한 사용자 검색 기반 음악 스트리밍 웹 서비스입니다.

음악 차트 서비스의 백엔드 API 서버를 중심으로 설계되었으며 음악 검색, 차트 제공까지 스트리밍 서비스에 필요한 핵심 기능들을 구현하였습니다.

**프로젝트 목적**

- 대용량 음악 메타데이터를 기반으로 한 안정적인 API 서버 구축
- 사용자 검색 데이터를 활용한 인기 검색어 / 인기 차트 제공
- 사용자 맞춤형 음악 경험 제공을 위한 플레이리스트 및 좋아요 기능 구현

## 3. 주요 기술 스택

- Java 17
- Spring Boot 3.5.9
- Postman 11.79.3
- JWT 0.13.0
- MySQL 8.4
- Docker 29.1.2
- QueryDSL

## 4. 데이터셋 추가

…

## 5. 실행 환경

### i. Redis 실행

Docker로 redis 띄우기

```json
docker pull redis:latest
docker run -d -p 6379:6379 --name redis-container redis:latest
```

### ii. Indexing

메인 패키지에 포함된 .sql 파일이 애플리케이션 실행 시 자동 수행되도록 구성되어 있어 추가적인 설정 없이 서버 실행만으로 처리 가능

### iii. Application 실행

```json
./gradlew bootRun
```

---

>

## 6. WireFrame + ERD

---

<img width="909" height="810" alt="image" src="https://github.com/user-attachments/assets/38c7a201-5aba-4477-9f85-ce8973a77995" />


<img width="1840" height="831" alt="ERD최종" src="https://github.com/user-attachments/assets/18174fe3-3b87-4707-8e1f-6fdca7aa5a41" />

---

## 7. 주요 기능

---

### 1) 노래

- `/api/songs`

— 노래 재생 : +`/{songId}/play` (Post)

— TOP10 음악 조회 : +`/top/v2` (Get)

— 최신 국내 / 해외 음악 조회 : +`/korea/new`, +`/global/new` (Get)

— 국내/해외+장르별 음악 조회 : +`/korea`, +`/global` (Get)

### 2) 앨범

- `/api/albums`

— 최신 앨범 조회 : +`/new` (Get)

— 앨범 상세 조회 : +`/{albumId}` (Get)

### 3) 플레이리스트

- `/api/playlists`

— 플레이리스트 생성 : (Post)

— 플레이리스트 목록 조회 : (Get)

— 플레이리스트 상세 조회 : +`/{playlistId}` (Get)

— 플레이리스트 수정 : +`/{playlistId}` (Put)

— 플레이리스트 삭제 : +`/{playlistId}` (Delete)

— 플레이리스트에 노래 추가 : +`/{playlistId}/songs/{songId}` (Post)

— 플레이리스트에 노래 삭제 : +`/{playlistId}/songs/{songId}` (Delete)

— 플레이리스트 노래 조회 : +`/{playlistId}/songs` (Get)

### 4) 검색

- `/api/search`

— 통합 검색 : 

— 인기 검색어 : 

### 5) 좋아요

- `/api/likes`

— 노래 좋아요 생성 : +`/{songId}` (Post)

— 노래 좋아요 삭제 : +`/{songId}` (Delete)

### 6) 사용자 및 인증/인가

- `/api/me`

— 로그인 : `/api/auth/login` (Post)

— 회원 가입 : +`/signup` (Post)

— 회원 탈퇴 : +`/withdraw` (Delete)

— 사용자 정보 조회 : +`/profile` (Get)

— 사용자 정보 수정 : +`/profile` (Put)

— 사용자 좋아요 음악 조회 : `/likes` (Get)

---

## 8. API 명세

---

[노션링크 첨부](https://www.notion.so/teamsparta/4-4-1-2cb2dc3ef514801e9b00d28c46460418?source=copy_link)


<img width="1186" height="757" alt="image" src="https://github.com/user-attachments/assets/5f04e990-9bfd-49ee-a2af-029d53e5bc82" />
<img width="1275" height="879" alt="image" src="https://github.com/user-attachments/assets/39bc9ea5-601b-4e49-8bb3-55e312321736" />
<img width="1205" height="367" alt="image" src="https://github.com/user-attachments/assets/c293b6b8-0a6e-4b98-92cb-e40a1747b8d4" />

---

## 9. 트러블 슈팅 & 최적화 전략

---

상무님/진수님은 본인이 작성한 ppt내용 참고

### 1) DataSet을 Local DB에 넣기까지의 과정 (진수님)

0. application.seed.yml

<img width="342" height="87" alt="image" src="https://github.com/user-attachments/assets/4d43f2fc-43a9-4573-8307-05694481d1f0" />

    
- dir : 해당 디렉터리 경로
- limit : 데이터 삽입 숫자.
1. Csvs.java

- open() : 첫 줄 헤더를 컬럼 명으로 매핑.

※ 첫 줄 헤더를 데이터로 넣지 않게끔 정리.

- bomAwareReader : 첫 세 바이트(byte sequence)가 UTF-8 BOM → 읽지 않고 넘기기. 맞다면 되감기.
- overLimit : limit = 0 : 무제한.
2. SeedRunner
- dir, limit를 yml에 받아온 다음 CommandLineRunner 메서드 run() 상속, SeedService.seedAll() 호출.
3. SeedService

<img width="763" height="683" alt="image" src="https://github.com/user-attachments/assets/a254be3f-8436-4ea9-a6b4-a79ab130ce3b" />

- seedAll() : 흐름 제어(Orchestration)
- 본 테이블 채우기 : private helper method

※ ex). seedSongs(), seedAlbums()

- → 각 테이블 별 row에서 id, externalId 매핑 : Repository  → ID Map
- → 조인 테이블 저장 : extract → map으로 PK 찾기 → getReference → persist로 적재.
- 간단한 정규화 : parseReleaseDateOrNull() : ‘-’ 단위 구분 후 LocalDate 객체 만들어서 반환.
4. Repository
- IdRow : Entity 전체가 아닌, externalId, id row만 적재.
- loadIdMap() : 엔트리가 늘 경우, 내부 배열이 증가하면서 재해시 비용 지불.

※ 기본 load factor : 0.75 

→ N개를 넣을 경우, N / 0.75로 잡기 → 중간 resize 소요 없이 한 번에 적재.

### 2) Redis (상무님)
# 1. 문제 원인

기존 V1 API는 인기 곡 목록을 조회하기 위해 **DB에서 `ORDER BY` 절을 사용한 정렬 쿼리**를 수행하고 있었다.

```jsx
SELECT ...
FROM songs
ORDER BY play_count DESC
LIMIT 100;
```

이 방식은 데이터 양이 적을 때는 문제가 없으나, 데이터가 증가할수록 다음과 같은 구조적 한계를 가진다.

### ❗ ORDER BY 기반 조회의 문제점

1. 정렬 비용 증가
    1. `ORDER BY`는 DB 내부에서 정렬 연산을 수행
    2. 정렬 대상 데이터가 많아질수록 CPU / 메모리 사용량 급증
2. 동시 요청 시 부하 집중
    1. TOP 10 목록 조 API는 조회 빈도가 높음
    2. 다수 요청이 동시에 들어오면
        1. 동일한 정렬 쿼리가 반복 실행
        2. DB에 부하가 순간적으로 집중됨

이로 인해 트래픽이 증가할수록 **DB가 병목 지점(Bottleneck)**이 되었고,

부하 테스트 환경에서는 **DB 스파이크 현상**으로 이어졌다.

---

# 2. 기술 도입

ORDER BY 기반 조회의 성능 한계를 해결하기 위해 다음과 같은 구조 개선을 진행하였다.

### 1️⃣ 정렬 결과 사전 계산 구조 도입

- 실시간 정렬(`ORDER BY`)을 제거
- 인기 곡 순위를 **미리 계산하여 저장**
- 요청 시:
    - 계산된 결과를 단순 조회

## 2️⃣ 캐시 기반 조회 구조 (V2)

- 조회 요청 시 DB 정렬 수행 ❌
- 캐시(Redis 등)에 저장된 정렬 결과를 바로 응답 ⭕

### 3️⃣ 동일 조건 부하 테스트 수행

- V1 / V2 모두 동일한 요청 패턴으로 부하 테스트 수행
- 비교 대상은 **정렬 방식 차이만 존재**
    - V1: ORDER BY 실시간 정렬
    - V2: 사전 계산 + 캐시 조회

이를 통해 구조 변경에 따른 성능 차이를 명확히 검증하였다.

---

# 3. 도입 전후 비교

### 도입 전 (V1 – ORDER BY 기반 조회)

![image.png](https://github.com/user-attachments/assets/66f5434a-ec33-4478-b85e-579db34a828e)

- 요청마다 DB에서 정렬 수행
- 데이터 증가 시 응답 시간 선형적 증가
- P95 응답 시간 급격히 증가
- 트래픽 증가 시 DB CPU 사용률 급증
- DB 스파이크 발생

## 도입 후 (V2 – 사전 계산 + 캐시)

![image.png](https://github.com/user-attachments/assets/731c953e-ec85-4d15-9514-5ec2bc46d20d)

- 정렬 연산 제거
- 단순 조회로 응답 처리
- 평균 응답 시간 대폭 감소
- 고부하 상황에서도 응답 시간 안정적
- DB 스파이크 미발생

---

# 4. 성능 개선 요약

| 항목 | V1 (ORDER BY) | V2 (사전 계산) |
| --- | --- | --- |
| 정렬 방식 | 실시간 DB 정렬 | 사전 계산 |
| DB 부하 | 높음 | 낮음 |
| 응답 시간 | 불안정 | 안정적 |
| 트래픽 대응 | 취약 | 우수 |
| 확장성 | 낮음 | 높음 |

---

# 5. 결론

프로젝트에서는 **ORDER BY 기반 실시간 정렬 조회가 고트래픽 환경에서 성능 병목을 유발**함을 확인하였다.

정렬 결과를 사전에 계산하고 캐시 기반으로 조회하는 구조로 개선함으로써,

- DB 부하 제거
- 응답 시간 안정화
- 대량 트래픽 환경에서도 일관된 성능 유지

라는 성능 개선 효과를 달성하였다.

ORDER BY 기반 실시간 정렬 조회는 데이터 규모와 트래픽 증가에 취약하며, 사전 계산 및 캐시 구조를 도입함으로써 시스템 확장성과 성능을 크게 개선할 수 있었다.

### 3) 플레이리스트 곡 목록 조회 Redis 사용
#### 1️⃣ 첫 페이지 캐시 구조 도입

- 플레이리스트 조회 중 0페이지를 Redis에 응답 결과를 캐싱
- 첫 페이지( 0 page )가 가장 많이 조회
- 플레이리스트 첫 페이지는 조회 빈도가 높고 변경 빈도가 낮음 → Cache 사용에 유리

#### 2️⃣ Cache-Aside 패턴 적용

- 요청 시:
    1. Redis 캐시 조회
    2. 캐시 존재 → Redis에서 즉시 반환
    3. 캐시 미존재 → DB 조회 후 Redis 저장

#### 3️⃣ 쓰기 시 캐시 무효화

- 곡 추가 / 삭제 시 해당 플레이리스트의 0페이지 캐시 삭제

#### 4️⃣부하 테스트

- V1: Redis 미사용(DB 직접 조회)
- V2: Redis 캐시 사용

---

#### Redis X

<img width="1478" height="656" alt="image" src="https://github.com/user-attachments/assets/9303dc90-458b-4c92-869e-1429f6e5b414" />

평균 응답 시간: 394ms
P95 응답 시간: 453ms

#### Redis O

<img width="1538" height="675" alt="image" src="https://github.com/user-attachments/assets/550eb909-2439-452e-8a12-3d058c31092a" />


평균 응답 시간: 15ms
P95 응답 시간: 44ms


### 4) Indexing (동&정)

구글 닥스 공유 문서 링크 + 간략한 설명과 지표 이미지 첨부

---

## 10. 👤 마치며

---

#### Spring plus 팀 프로젝트
- 이상무, 김동욱, 임정하, 장서연, 탁진수

[ppt 링크](https://www.canva.com/design/DAG9zTXiQRw/AYF-984bvg368r9vemWnew/edit?utm_content=DAG9zTXiQRw&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

(각자 git 하이퍼링크 달면 좋습니다.)
