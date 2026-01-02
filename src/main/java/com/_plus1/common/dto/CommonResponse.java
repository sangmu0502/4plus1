package com._plus1.common.dto;

import com._plus1.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommonResponse<T> {

    private final boolean success;         // 성공 여부
    private final String message;          // 성공시 요청이 성공했습니다. , 실패시 error message
    private final LocalDateTime timestamp; // 실행 시간
    private final T data;                  // 성공 시 data , 실패시 null

    private CommonResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // 성공시 공용 응답 객체
    public static <T> CommonResponse<T> success(T data, String message) {
        return new CommonResponse<>(true, message, data, LocalDateTime.now());
    }

    // 실패시 공용 응답 객체
    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return new CommonResponse<>(false, errorCode.getMessage(), null, LocalDateTime.now());
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(false, message, null, LocalDateTime.now());
    }

    // 메세지를 직접 입력하는 경우
    public static <T> CommonResponse<T> fail(ErrorCode errorCode, String message) {
        return new CommonResponse<>(
                false,
                message,
                null,
                LocalDateTime.now()
        );
    }

    // data가 null일 때 사용하는 응답 객체
    public static <T> CommonResponse<T> success(String message){
        return new CommonResponse<>(true, message, null, LocalDateTime.now());
    }
}
