package com._plus1.domain.song.model.enums;

import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

// 해당 파일이 song 도메인쪽에 있는 것에 의문이 들 수 있는데,
// common이 들고있을 지식은 아니고 + 장르 패키지가 없으니 = 일단 이곳에 위치시켰습니다.
// 이렇게 관리하는 편이 나중에 수정하기 편하다고 생각하는데, 더 나은 방법이 있다면 추후에 리팩토링하겠습니다. # 20260106 김동욱
public enum KoreanPopularGenreCode {

    // 수정 시, 최하단의 주석을 참고하고 함께 수정할 것!

    // 발라드
    BALLAD(
            "발라드",
            List.of("GN0100", "GN0101", "GN0102", "GN0103", "GN0104", "GN0105")
    ),

    // 댄스
    DANCE(
            "댄스",
            List.of("GN0200", "GN0201", "GN0202", "GN0203", "GN0204", "GN0205")
    ),

    // 랩/힙합
    HIPHOP(
            "랩/힙합",
            List.of("GN0300", "GN0301", "GN0302", "GN0303", "GN0304", "GN0305")
    ),

    // R&B / Soul
    RNB_SOUL(
            "R&B/Soul",
            List.of("GN0400", "GN0401", "GN0402", "GN0403")
    ),

    // 인디
    INDIE(
            "인디",
            List.of(
                    "GN0500", "GN0501", "GN0502", "GN0503", "GN0504",
                    "GN0505", "GN0506", "GN0507", "GN0508", "GN0509"
            )
    ),

    // 포크 / 블루스 (※ 코드상 GN0900 계열)
    FOLK_BLUES(
            "포크/블루스",
            List.of(
                    "GN0900", "GN0901", "GN0902", "GN0903", "GN0904",
                    "GN0905", "GN0906", "GN0907", "GN0908"
            )
    ),

    // 트로트
    TROT(
            "트로트",
            List.of("GN1500", "GN1501", "GN1502", "GN1504", "GN1507")
    ),

    // OST
    OST(
            "OST",
            List.of(
                    "GN2500", "GN2501", "GN2502",
                    "GN2503", "GN2504", "GN2505", "GN2506"
            )
    ),

    // 키즈
    KIDS(
            "키즈",
            List.of("GN2900", "GN2901", "GN2902")
    );

    private final String displayName;
    private final List<String> genreCodes;

    KoreanPopularGenreCode(String displayName, List<String> genreCodes) {
        this.displayName = displayName;
        this.genreCodes = genreCodes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getGenreCodes() {
        return genreCodes;
    }

//=====================================================이전 코드==========================================================
//    // 발라드
//    GN0100, GN0101, GN0102, GN0103, GN0104, GN0105,
//
//    // 댄스
//    GN0200, GN0201, GN0202, GN0203, GN0204, GN0205,
//
//    // 랩/힙합
//    GN0300, GN0301, GN0302, GN0303, GN0304, GN0305,
//
//    // R&B/Soul
//    GN0400, GN0401, GN0402, GN0403,
//
//    // 인디
//    GN0500, GN0501, GN0502, GN0503, GN0504, GN0505,
//    GN0506, GN0507, GN0508, GN0509,
//
//    // 포크/블루스
//    GN0900, GN0901, GN0902, GN0903, GN0904, GN0905,
//    GN0906, GN0907, GN0908,
//
//    // 트로트
//    GN1500, GN1501, GN1502, GN1504, GN1507,
//
//    // OST
//    GN2500, GN2501, GN2502, GN2503, GN2504, GN2505, GN2506,
//
//    // 키즈
//    GN2900, GN2901, GN2902;
//
//    // JPA 조회용 문자열 코드 리스트
//    public static List<String> codes() {
//        return Stream.of(values())
//                .map(Enum::name)
//                .toList();
//    }
//======================================================================================================================

//    한국 대중음악 범위 :
//            "GN0100": "발라드",
//            "GN0101": "세부장르전체",
//            "GN0102": "'80",
//            "GN0103": "'90",
//            "GN0104": "'00",
//            "GN0105": "'10-",
//
//            "GN0200": "댄스",
//            "GN0201": "세부장르전체",
//            "GN0202": "'80",
//            "GN0203": "'90",
//            "GN0204": "'00",
//            "GN0205": "'10-",
//
//            "GN0300": "랩/힙합",
//            "GN0301": "세부장르전체",
//            "GN0302": "랩 스타일",
//            "GN0303": "보컬 스타일",
//            "GN0304": "언더그라운드 힙합",
//            "GN0305": "시대별",
//
//            "GN0400": "R&B/Soul",
//            "GN0401": "세부장르전체",
//            "GN0402": "어반",
//            "GN0403": "R&B",
//
//            "GN0500": "인디음악",
//            "GN0501": "세부장르전체",
//            "GN0502": "포크",
//            "GN0503": "록",
//            "GN0504": "일렉",
//            "GN0505": "힙합",
//            "GN0506": "발라드",
//            "GN0507": "'90",
//            "GN0508": "'00",
//            "GN0509": "'10-",
//
//            "GN0900": "POP",
//            "GN0901": "세부장르전체",
//            "GN0902": "얼터너티브팝",
//            "GN0903": "올디스",
//            "GN0904": "월드팝",
//            "GN0905": "'60-'70",
//            "GN0906": "'80-'90",
//            "GN0907": "'00",
//            "GN0908": "'10-",
//
//            "GN1500": "OST",
//            "GN1501": "세부장르전체",
//            "GN1502": "국내영화",
//            "GN1504": "국내드라마",
//            "GN1507": "국내뮤지컬",
//
//            "GN2500": "아이돌",
//            "GN2501": "세부장르전체",
//            "GN2502": "남자 아이돌",
//            "GN2503": "여자 아이돌",
//            "GN2504": "랩/힙합",
//            "GN2505": "발라드",
//            "GN2506": "댄스",
//
//            "GN2900": "뮤지컬",
//            "GN2901": "세부장르전체",
//            "GN2902": "국내뮤지컬"

}
