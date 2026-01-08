import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const SIZE = parseInt(__ENV.SIZE || "10", 10);
const MIXED = (__ENV.MIXED || "0") === "1";
const PAGE_SPREAD = parseInt(__ENV.PAGE_SPREAD || "5", 10);

const Q_FIXED = (__ENV.Q || "hello").trim();
const Q_POOL = (__ENV.Q_POOL || "hello,world,test,foo,bar,baz")
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);

const Q_MODE = (__ENV.Q_MODE || "rr").trim(); // fixed | rr | random

function pickQuery() {
    if (Q_MODE === "fixed") return Q_FIXED;
    if (Q_MODE === "random") return Q_POOL[Math.floor(Math.random() * Q_POOL.length)];
    // rr
    return Q_POOL[(__ITER + (__VU - 1)) % Q_POOL.length];
}

function pickPage() {
    if (!MIXED) return 0;
    return Math.floor(Math.random() * PAGE_SPREAD);
}

const CLEAR_CACHE_PATH = __ENV.CLEAR_CACHE_PATH || ""; // 예: "/api/cache/clear"
function clearCacheOnce() {
    if (!CLEAR_CACHE_PATH) return;
    if (__VU === 1 && __ITER === 0) {
        const r = http.get(`${BASE_URL}${CLEAR_CACHE_PATH}`);
        check(r, { "cache cleared (200)": (res) => res.status === 200 });
    }
}

function get(path, tags) {
    const url = `${BASE_URL}${path}`;
    const res = http.get(url, { tags });
    check(res, { "status is 200": (r) => r.status === 200 });
    return res;
}

// ENV로 어떤 시나리오만 돌릴지 지정
// 예시 : -e TARGET=mysql_slice_off
const TARGET = (__ENV.TARGET || "all").trim();

const ALL = {
    mysql_page_off: { executor: "constant-vus", vus: 10, duration: "30s", exec: "mysqlPageOff",  },
    mysql_page_on:  { executor: "constant-vus", vus: 10, duration: "30s", exec: "mysqlPageOn", startTime: "35s",},
    mysql_slice_off:{ executor: "constant-vus", vus: 10, duration: "30s", exec: "mysqlSliceOff", startTime: "70s", },
    mysql_slice_on: { executor: "constant-vus", vus: 10, duration: "30s", exec: "mysqlSliceOn", startTime: "105s", },
    es_slice:       { executor: "constant-vus", vus: 10, duration: "30s", exec: "esSlice", startTime: "140s", },
};

// TARGET이 all이면 전부 실행, 아니면 해당 1개만 실행
const scenarios =
    TARGET === "all"
        ? ALL
        : { [TARGET]: ALL[TARGET] };

// TARGET 오타 방지: 잘못된 값이면 즉시 실패시키기
if (!scenarios || Object.keys(scenarios).length === 0 || !Object.values(scenarios)[0]) {
    throw new Error(
        `Invalid TARGET='${TARGET}'. Use one of: ${Object.keys(ALL).join(", ")} or 'all'.`
    );
}

export const options = {
    scenarios,
    thresholds: {
        http_req_failed: ["rate<0.01"],

        // 태그별 sub-metric 생성(출력 분리용)
        "http_req_duration{backend:mysql,mode:page,cache:off}": ["p(95)<800"],
        "http_req_duration{backend:mysql,mode:page,cache:on}": ["p(95)<800"],
        "http_req_duration{backend:mysql,mode:slice,cache:off}": ["p(95)<800"],
        "http_req_duration{backend:mysql,mode:slice,cache:on}": ["p(95)<800"],
        "http_req_duration{backend:es,mode:slice}": ["p(95)<800"],
    },
    discardResponseBodies: true,
};

export function mysqlPageOff() {
    clearCacheOnce();
    const q = pickQuery();
    const page = pickPage();
    get(
        `/api/search?q=${encodeURIComponent(q)}&page=${page}&size=${SIZE}`,
        { backend: "mysql", mode: "page", cache: "off", name: "mysql_page_off" }
    );
    sleep(0.1);
}

export function mysqlPageOn() {
    clearCacheOnce();
    const q = pickQuery();
    const page = pickPage(); // 주의: page>0이면 네 cacheable 조건 때문에 캐시 안 탐(그게 의도면 OK)
    get(
        `/api/search/cache?q=${encodeURIComponent(q)}&page=${page}&size=${SIZE}`,
        { backend: "mysql", mode: "page", cache: "on", name: "mysql_page_on" }
    );
    sleep(0.1);
}

export function mysqlSliceOff() {
    clearCacheOnce();
    const q = pickQuery();
    const page = pickPage();
    get(
        `/api/search/slice?q=${encodeURIComponent(q)}&page=${page}&size=${SIZE}`,
        { backend: "mysql", mode: "slice", cache: "off", name: "mysql_slice_off" }
    );
    sleep(0.1);
}

export function mysqlSliceOn() {
    clearCacheOnce();
    const q = pickQuery();
    const page = pickPage();
    get(
        `/api/search/slice/cache?q=${encodeURIComponent(q)}&page=${page}&size=${SIZE}`,
        { backend: "mysql", mode: "slice", cache: "on", name: "mysql_slice_on" }
    );
    sleep(0.1);
}

export function esSlice() {
    clearCacheOnce();
    const q = pickQuery();
    const page = pickPage();
    get(
        `/api/search/es?q=${encodeURIComponent(q)}&page=${page}&size=${SIZE}`,
        { backend: "es", mode: "slice", cache: "off", name: "es_slice" }
    );
    sleep(0.1);
}
// 분산
// k6 run `
//   --summary-export=summary.json `
// --summary-trend-stats="min,med,p(90),p(95),p(99),max,count" --summary-time-unit=ms `
//   -e BASE_URL=http://localhost:8080 `
//     -e Q_POOL="hello,world,test,foo,bar,baz" -e Q_MODE=rr `
//   -e SIZE=10 -e MIXED=0 -e PAGE_SPREAD=5 `
//     .\search_bench.js

// 고정
// k6 run `
//   --summary-export=summary_fixed.json `
// --summary-trend-stats="min,med,p(90),p(95),p(99),max,count" --summary-time-unit=ms `
//   -e BASE_URL=http://localhost:8080 `
//     -e Q=hello -e Q_MODE=fixed `
//   -e SIZE=10 -e MIXED=0 `
//     .\search_bench.js
