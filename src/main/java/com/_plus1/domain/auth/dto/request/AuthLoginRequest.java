package com._plus1.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AuthLoginRequest {

    @NotBlank(message = "아이디는 반드시 입력해야 합니다.")
    public String nickname;

    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    public String password;
}
