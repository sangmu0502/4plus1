package com._plus1.domain.user.dto.request;

import lombok.Getter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
public class UserSignupRequest {

    @NotBlank(message = "사용자 이름은 반드시 입력해야 합니다.")
    @Size(min = 1, max = 30, message = "사용자 이름은 1자 이상 30자 이하여야 합니다.")
    private String userName;

    @NotBlank(message = "이메일은 반드시 입력해야 합니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]|;:'\",.<>/?]).{8,16}$", message = "비밀번호는 8자 이상 16자 이하 영문 대소문자, 숫자, 특수문자를 사용해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 반드시 입력해야 합니다.")
    @Size(min = 3, max = 30, message = "닉네임은 3자 이상 30자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "휴대폰 번호는 반드시 입력해야 합니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", message = "휴대폰 번호 형식이 올바르지 않습니다. (하이픈 제외 10~11자리)")
    private String phoneNumber;
}
