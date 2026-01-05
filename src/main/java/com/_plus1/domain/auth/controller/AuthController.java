package com._plus1.domain.auth.controller;

import com._plus1.common.dto.CommonResponse;
import com._plus1.domain.auth.dto.request.AuthLoginRequest;
import com._plus1.domain.auth.dto.response.TokenResponse;
import com._plus1.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponse>> loginApi(@Valid @RequestBody AuthLoginRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(authService.login(request), "로그인에 성공했습니다."));
    }
}
