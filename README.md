

# 🎵 Sparta Music API

## 0. 목차

1. 팀원 소개
2. 프로젝트 개요
3. 주요 기술 스택
4. 데이터셋 추가
5. 실행 환경
6. ERD
7. 주요 기능
8. API 명세
9. 트러블 슈팅 & 최적화 전략
10. 마치며

<br>

## 1. 팀원 및 역할 소개

| 🚩이상무 | 김동욱 | 임정하 | 장서연 | 탁진수 |
| --- | --- | --- | --- | --- |
| 노래 API<br>좋아요 API | 앨범 API<br>장르 노래 API | 인증 / 인가<br>유저 API | 플레이리스트 API | 검색 API |

<br>

## 2. 프로젝트 개요

본 프로젝트는 멜론에서 제공하는 약 70만 건의 음악 메타데이터셋을 가공하여 구축한 사용자 검색 기반 음악 스트리밍 웹 서비스입니다.

음악 차트 서비스의 백엔드 API 서버를 중심으로 설계되었으며 음악 검색, 차트 제공까지 스트리밍 서비스에 필요한 핵심 기능들을 구현하였습니다.

**[ 프로젝트 목적 ]**

- 대용량 음악 메타데이터를 기반으로 한 안정적인 API 서버 구축
- 사용자 검색 데이터를 활용한 인기 검색어 / 인기 차트 제공
- 사용자 맞춤형 음악 경험 제공을 위한 플레이리스트 및 좋아요 기능 구현

<br>

## 3. 주요 기술 스택

- Java 17
- Spring Boot 3.5.9
- Postman 11.79.3
- JWT 0.13.0
- MySQL 8.4
- Docker 29.1.2
- QueryDSL 5.0.0
- ElasticSearch 8.12.2
- redis 7.4.7
- csv 1.10.0

<br>

## 4. 데이터셋 추가

<img width="689" height="33" alt="image" src="https://github.com/user-attachments/assets/d2c4fcfc-1f8a-4733-b5d7-ac4562d1d8e5" />
### i. git clone -b search-tjs --single-branch https://github.com/sangmu0502/4plus1.git

<img width="327" height="589" alt="image" src="https://github.com/user-attachments/assets/dd5b7273-0ecf-4dff-905c-40b5784ab428" />
<img width="802" height="693" alt="image" src="https://github.com/user-attachments/assets/70716265-2d73-40d6-8cef-b9fde02b16cf" />
### ii. Alt + U + R -> 활성화된 프로파일 : seed 입력

### iii. ./gradlew bootRun


<br>

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

<br>

## 6. WireFrame + ERD

<img width="909" height="810" alt="image" src="https://github.com/user-attachments/assets/38c7a201-5aba-4477-9f85-ce8973a77995" />

<img width="1840" height="831" alt="ERD최종" src="https://github.com/user-attachments/assets/18174fe3-3b87-4707-8e1f-6fdca7aa5a41" />

<br>

## 7. 주요 기능

### 1) 사용자 및 인증/인가
<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/me` (일부 별도 경로)

* **POST** `/api/auth/login` : 로그인
* **POST** `/signup` : 회원 가입
* **DELETE** `/withdraw` : 회원 탈퇴
* **GET** `/profile` : 사용자 정보 조회
* **PUT** `/profile` : 사용자 정보 수정
* **GET** `/likes` : 사용자 좋아요 음악 조회
</details>

### 2) 노래

<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/songs`

* **POST** `/{songId}/play` : 노래 재생
* **GET** `/top/v2` : TOP10 음악 조회
* **GET** `/korea/new` : 최신 국내 음악 조회
* **GET** `/global/new` : 최신 해외 음악 조회
* **GET** `/korea` : 국내 장르별 음악 조회
* **GET** `/global` : 해외 장르별 음악 조회

</details>

### 3) 앨범
<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/albums`

* **GET** `/new` : 최신 앨범 조회
* **GET** `/{albumId}` : 앨범 상세 조회
</details>

### 4) 플레이리스트
<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/playlists`

* **POST** `기본 경로` : 플레이리스트 생성
* **GET** `기본 경로` : 플레이리스트 목록 조회
* **GET** `/{playlistId}` : 플레이리스트 상세 조회
* **PUT** `/{playlistId}` : 플레이리스트 수정
* **DELETE** `/{playlistId}` : 플레이리스트 삭제
* **POST** `/{playlistId}/songs/{songId}` : 플레이리스트에 노래 추가
* **DELETE** `/{playlistId}/songs/{songId}` : 플레이리스트에서 노래 삭제
* **GET** `/{playlistId}/songs` : 플레이리스트 내 노래 목록 조회
</details>

### 5) 검색
<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/search`

* **GET** `기본 경로` : 통합 검색
* **GET** `/popular` : 인기 검색어 조회
</details>

### 6) 좋아요
<details>
<summary>API 상세 보기</summary>

- 기본 경로: `/api/likes`

* **POST** `/{songId}` : 노래 좋아요 추가
* **DELETE** `/{songId}` : 노래 좋아요 취소
</details>

<br>

## 8. API 명세

- [노션링크 첨부](https://www.notion.so/teamsparta/4-4-1-2cb2dc3ef514801e9b00d28c46460418?source=copy_link)

<img width="1186" height="757" alt="image" src="https://github.com/user-attachments/assets/5f04e990-9bfd-49ee-a2af-029d53e5bc82" />
<img width="1275" height="879" alt="image" src="https://github.com/user-attachments/assets/39bc9ea5-601b-4e49-8bb3-55e312321736" />
<img width="1205" height="367" alt="image" src="https://github.com/user-attachments/assets/c293b6b8-0a6e-4b98-92cb-e40a1747b8d4" />

<br>

## 9. 트러블 슈팅 & 최적화 전략

**[ DataSet -> Local DB에 넣기까지의 과정 ]**

**1️⃣ application.seed.yml**

<img width="342" height="87" alt="image" src="https://github.com/user-attachments/assets/4d43f2fc-43a9-4573-8307-05694481d1f0" />

    
- dir : 해당 디렉터리 경로
- limit : 데이터 삽입 숫자.

  
**2️⃣ Csvs.java**

- open() : 첫 줄 헤더를 컬럼 명으로 매핑.

※ 첫 줄 헤더를 데이터로 넣지 않게끔 정리.

- bomAwareReader : 첫 세 바이트(byte sequence)가 UTF-8 BOM → 읽지 않고 넘기기. 맞다면 되감기.
- overLimit : limit = 0 : 무제한.

  
**3️⃣ SeedRunner**
- dir, limit를 yml에 받아온 다음 CommandLineRunner 메서드 run() 상속, SeedService.seedAll() 호출.


**4️⃣ SeedService**

<img width="763" height="683" alt="image" src="https://github.com/user-attachments/assets/a254be3f-8436-4ea9-a6b4-a79ab130ce3b" />

- seedAll() : 흐름 제어(Orchestration)
- 본 테이블 채우기 : private helper method

※ ex). seedSongs(), seedAlbums()

- → 각 테이블 별 row에서 id, externalId 매핑 : Repository  → ID Map
- → 조인 테이블 저장 : extract → map으로 PK 찾기 → getReference → persist로 적재.
- 간단한 정규화 : parseReleaseDateOrNull() : ‘-’ 단위 구분 후 LocalDate 객체 만들어서 반환.

  
**5️⃣ Repository**
- IdRow : Entity 전체가 아닌, externalId, id row만 적재.
- loadIdMap() : 엔트리가 늘 경우, 내부 배열이 증가하면서 재해시 비용 지불.

※ 기본 load factor : 0.75 

→ N개를 넣을 경우, N / 0.75로 잡기 → 중간 resize 소요 없이 한 번에 적재.

<br>

---

<br>

### 2) Redis
**1️⃣ 문제 원인**

기존 V1 API는 인기 곡 목록을 조회하기 위해 **DB에서 `ORDER BY` 절을 사용한 정렬 쿼리**를 수행하고 있었다.

```jsx
SELECT ...
FROM songs
ORDER BY play_count DESC
LIMIT 100;
```

이 방식은 데이터 양이 적을 때는 문제가 없으나, 데이터가 증가할수록 다음과 같은 구조적 한계를 가진다.

#### ❗ ORDER BY 기반 조회의 문제점

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


**2️⃣ 기술 도입**

ORDER BY 기반 조회의 성능 한계를 해결하기 위해 다음과 같은 구조 개선을 진행하였다.

1. 정렬 결과 사전 계산 구조 도입

- 실시간 정렬(`ORDER BY`)을 제거
- 인기 곡 순위를 **미리 계산하여 저장**
- 요청 시:
    - 계산된 결과를 단순 조회

2. 캐시 기반 조회 구조 (V2)

- 조회 요청 시 DB 정렬 수행 ❌
- 캐시(Redis 등)에 저장된 정렬 결과를 바로 응답 ⭕

3. 동일 조건 부하 테스트 수행

- V1 / V2 모두 동일한 요청 패턴으로 부하 테스트 수행
- 비교 대상은 **정렬 방식 차이만 존재**
    - V1: ORDER BY 실시간 정렬
    - V2: 사전 계산 + 캐시 조회

이를 통해 구조 변경에 따른 성능 차이를 명확히 검증하였다.


**3️⃣ 도입 전후 비교**

**[ 도입 전 ]**
- V1 – ORDER BY 기반 조회

![image.png](https://github.com/user-attachments/assets/66f5434a-ec33-4478-b85e-579db34a828e)

- 요청마다 DB에서 정렬 수행
- 데이터 증가 시 응답 시간 선형적 증가
- P95 응답 시간 급격히 증가
- 트래픽 증가 시 DB CPU 사용률 급증
- DB 스파이크 발생

**[ 도입 후 ]**
- V2 – 사전 계산 + 캐시

![image.png](https://github.com/user-attachments/assets/731c953e-ec85-4d15-9514-5ec2bc46d20d)

- 정렬 연산 제거
- 단순 조회로 응답 처리
- 평균 응답 시간 대폭 감소
- 고부하 상황에서도 응답 시간 안정적
- DB 스파이크 미발생

**4️⃣ 성능 개선 요약**

| 항목 | V1 (ORDER BY) | V2 (사전 계산) |
| --- | --- | --- |
| 정렬 방식 | 실시간 DB 정렬 | 사전 계산 |
| DB 부하 | 높음 | 낮음 |
| 응답 시간 | 불안정 | 안정적 |
| 트래픽 대응 | 취약 | 우수 |
| 확장성 | 낮음 | 높음 |

**5️⃣ 결론**

프로젝트에서는 **ORDER BY 기반 실시간 정렬 조회가 고트래픽 환경에서 성능 병목을 유발**함을 확인하였다.

정렬 결과를 사전에 계산하고 캐시 기반으로 조회하는 구조로 개선함으로써,

- DB 부하 제거
- 응답 시간 안정화
- 대량 트래픽 환경에서도 일관된 성능 유지

라는 성능 개선 효과를 달성하였다.

ORDER BY 기반 실시간 정렬 조회는 데이터 규모와 트래픽 증가에 취약하며, 사전 계산 및 캐시 구조를 도입함으로써 시스템 확장성과 성능을 크게 개선할 수 있었다.

<br>

---

<br>

### 3) 플레이리스트 곡 목록 조회 Redis 사용
**1️⃣ 첫 페이지 캐시 구조 도입**

- 플레이리스트 조회 중 0페이지를 Redis에 응답 결과를 캐싱
- 첫 페이지( 0 page )가 가장 많이 조회
- 플레이리스트 첫 페이지는 조회 빈도가 높고 변경 빈도가 낮음 → Cache 사용에 유리

**2️⃣ Cache-Aside 패턴 적용**

- 요청 시:
    1. Redis 캐시 조회
    2. 캐시 존재 → Redis에서 즉시 반환
    3. 캐시 미존재 → DB 조회 후 Redis 저장

**3️⃣ 쓰기 시 캐시 무효화**

- 곡 추가 / 삭제 시 해당 플레이리스트의 0페이지 캐시 삭제

**4️⃣ 부하 테스트**

- V1: Redis 미사용(DB 직접 조회)
- V2: Redis 캐시 사용

**[ Redis X ]**
- 평균 응답 시간: 394ms
- P95 응답 시간: 453ms

<img width="1478" height="656" alt="image" src="https://github.com/user-attachments/assets/9303dc90-458b-4c92-869e-1429f6e5b414" />

**[ Redis O ]**
- 평균 응답 시간: 15ms
- P95 응답 시간: 44ms

<img width="1538" height="675" alt="image" src="https://github.com/user-attachments/assets/550eb909-2439-452e-8a12-3d058c31092a" />

<br>

---

<br>

### 4) Indexing

**1️⃣ 문제 원인**

[ 이미지 첨부 ]

- **풀 테이블 스캔 발생**: `genre_code`가 문자열(String) 컬럼임에도 인덱스가 없어, 장르별 검색 시 MySQL이 `genres` 테이블 전체를 스캔함
- **중간 테이블 조인 병목**: 곡(Song)과 장르(Genre)를 연결하는 `song_genre` 테이블의 데이터가 방대해짐에 따라, 조인 및 존재 여부 확인(`exists`) 쿼리에서 I/O 부하 발생
- **정렬 부하(Filesort)**: 최신순 정렬 시 인덱스가 없어 DB가 직접 데이터를 메모리에 올려 정렬하는 `Filesort`가 발생하며, 페이지 번호(Offset)가 뒤로 갈수록 응답 시간이 급격히 증가함
- **실제 체감 지연**: Postman 테스트 결과, 특정 조회 서비스에서 **최대 8.72초**의 응답 시간이 소요되어 사용자 경험을 저해함

**2️⃣ 기술 도입**

- **전략적 인덱스 설계**
    - `genres(genre_code)`: 문자열 풀 스캔 제거 및 즉시 탐색 유도
    - `song_genre(genre_id, song_id)`: 조인 최적화 및 `exists` 서브쿼리의 조기 종료(Early Exit) 유도
    - `songs(release_date DESC, id)`: `Filesort`를 제거하고 인덱스 스캔 기반의 안정적인 페이징 구현
    

**3️⃣ 도입 전후 비교**

<img width="721" height="309" alt="스크린샷 2026-01-09 10 25 16" src="https://github.com/user-attachments/assets/7133517a-e168-4e25-9ec5-ba2a12ff7d45" />


**4️⃣ 성능 개선 요약**

- **평균 응답 시간 :**
    - **Windows** : 평균 8.72초 → 약 1.89초 (**전체 약 78% 감소**)
    - **Mac** : 평균 4.13초 → 약 1.38초 (**전체 약 66% 감소**)
- **가장 높은 개선 효과 :**
    - D. genres (genre_code) + song_genre (genre_id, song_id) + songs (release_date, id)
    - **Windows** 기준 약 **91.9%**, **Mac** 기준 **85.5%**의 압도적인 속도 향상을 보임

<img width="880" height="270" alt="스크린샷 2026-01-09 11 12 09" src="https://github.com/user-attachments/assets/ef9c706e-5595-403e-88a0-f1c8bc4e85df" />

<br>

## 10. 마치며

[ppt 링크](https://www.canva.com/design/DAG9zTXiQRw/AYF-984bvg368r9vemWnew/edit?utm_content=DAG9zTXiQRw&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

### Spring plus 팀 프로젝트
- 이상무 : https://github.com/sangmu0502
- 김동욱 : https://github.com/BullGombo
- 임정하 : https://github.com/JH319
- 장서연 : https://github.com/jangse0
- 탁진수 : https://github.com/milestone9701-29
