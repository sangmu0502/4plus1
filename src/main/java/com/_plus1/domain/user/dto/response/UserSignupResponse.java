package com._plus1.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserSignupResponse {

    private Long id;
    private String name;
    private String email;
    private String nickname;
    private String phoneNumber;
    private LocalDateTime createdAt;

    public UserSignupResponse(Long id, String name, String email, String nickname, String phoneNumber, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }
}
