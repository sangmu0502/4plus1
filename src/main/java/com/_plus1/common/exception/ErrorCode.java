package com._plus1.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Auth(인증,권한) 관련 ErrorCode
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    ALREADY_LOGGED_IN(HttpStatus.CONFLICT, "이미 로그인이 되어 있습니다."),

    // Common 공통 ErrorCode
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다: "),

    // User 관련 ErrorCode
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // Search 관련 ErrorCode
    EMPTY_QUERY(HttpStatus.BAD_REQUEST, "검색어를 입력해주세요."),

    // Song 관련 ErrorCode
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요를 누른 노래입니다."),
    SONG_NOT_FOUND(HttpStatus.NOT_FOUND, "노래를 찾을 수 없습니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 찾을 수 없습니다."),

    // 장르 관련 ErrorCode
    INVALID_GENRE(HttpStatus.BAD_REQUEST, "유효한 장르명이 아닙니다."),
  
    // Album 관련 ErrorCode
    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 앨범을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}

