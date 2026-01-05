package com._plus1.domain.user.service;

import com._plus1.common.entity.User;
import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import com._plus1.domain.user.dto.request.UserSignupRequest;
import com._plus1.domain.user.dto.response.UserGetProfileResponse;
import com._plus1.domain.user.dto.response.UserSignupResponse;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public UserSignupResponse signup(UserSignupRequest request) {

        // 이미 가입된 이메일인 경우 예외 발생
        if (userRepository.existsByEmail(request.getEmail())) {
            log.info("이미 가입된 이메일입니다. {}", ErrorCode.DUPLICATE_EMAIL);
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUserName(),
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                request.getPhoneNumber()
        );

        User savedUser = userRepository.save(user);

        return new UserSignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getPhoneNumber(),
                savedUser.getCreatedAt()
        );

    }

    // 사용자 프로필 조회 로직
    @Transactional(readOnly = true)
    public UserGetProfileResponse getProfile(String nickname) {

        // 로그인할 때 사용한 닉네임으로 유저 조회
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserGetProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCreatedAt()
        );
    }
}
