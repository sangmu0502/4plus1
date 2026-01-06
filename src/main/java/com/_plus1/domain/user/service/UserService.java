package com._plus1.domain.user.service;

import com._plus1.common.entity.User;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 작성자 : 이상무
    // 유저 정보가 필요해서 임시로 불러올 유저 메서드 생성
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("더미 유저가 존재하지 않습니다."));
    }
}
