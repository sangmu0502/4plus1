package com._plus1.domain.auth.service;

import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.common.security.JwtUtil;
import com._plus1.domain.auth.dto.request.AuthLoginRequest;
import com._plus1.domain.auth.dto.response.TokenResponse;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtutil;

    @Transactional
    public TokenResponse login(AuthLoginRequest request) {

        // 회원조회
        User user = userRepository.findByNicknameAndIsDeletedFalse(request.getNickname())
                .orElseThrow(()-> new CustomException(ErrorCode.LOGIN_FAIL));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        // JWT 토큰 발급
        String token = jwtutil.createJwt(user.getNickname());

        return new TokenResponse(token);
    }
}
