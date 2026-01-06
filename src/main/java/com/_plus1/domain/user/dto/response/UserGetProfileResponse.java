package com._plus1.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserGetProfileResponse {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
