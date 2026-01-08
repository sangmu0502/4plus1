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

1. 멜론 데이터셋을 우리의 ERD에 맞게 Python코드로 CSV파일 7개로 추출 (이부분 코드를 첨부하고 싶다면 상무님에게 상담)
2. CSV파일을 Local DB에 넣는 과정 설명

### 2) Redis (상무님)

### 3) Redis (서연님)

### 4) Indexing (동&정)

구글 닥스 공유 문서 링크 + 간략한 설명과 지표 이미지 첨부

---

## 10. 👤 마치며

---

#### Spring plus 팀 프로젝트
- 이상무, 김동욱, 임정하, 장서연, 탁진수

[ppt 링크](https://www.canva.com/design/DAG9zTXiQRw/AYF-984bvg368r9vemWnew/edit?utm_content=DAG9zTXiQRw&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

(각자 git 하이퍼링크 달면 좋습니다.)
