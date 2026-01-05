package com._plus1.domain.user.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.common.security.UserDetailsImpl;
import com._plus1.domain.user.dto.request.UserSignupRequest;
import com._plus1.domain.user.dto.request.UserUpdateRequest;
import com._plus1.domain.user.dto.response.UserGetProfileResponse;
import com._plus1.domain.user.dto.response.UserSignupResponse;
import com._plus1.domain.user.dto.response.UserUpdateProfileResponse;
import com._plus1.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<UserSignupResponse>> signupApi(@Valid @RequestBody UserSignupRequest signupRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(userService.signup(signupRequest), "회원가입에 성공했습니다."));
    }

    // 사용자 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<CommonResponse<UserGetProfileResponse>> getProfileApi(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success(userService.getProfile(userDetails.getUsername()), "사용자 프로필 조회가 완료되었습니다."));
    }

    // 사용자 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<CommonResponse<UserUpdateProfileResponse>> updateProfileApi(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserUpdateRequest updateRequest) {

        // userDetails.getUser()를 통해 현재 로그인한 유저 엔티티를 서비스로 전달
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.success( userService.updateProfile(userDetails.getUser().getId(), updateRequest), "사용자 프로필 조회가 완료되었습니다."));
    }

}
