package com._plus1.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequest {

    private String userName;
    private String email;
    private String nickname;
    private String phoneNumber;
}
