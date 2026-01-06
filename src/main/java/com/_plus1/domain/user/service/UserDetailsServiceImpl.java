package com._plus1.domain.user.service;

import com._plus1.common.entity.User;
import com._plus1.common.security.UserDetailsImpl;
import com._plus1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security가 DB에서 유저를 찾을 때 사용하는 서비스
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNicknameAndIsDeletedFalse(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다:" + nickname));

        if (user.isDeleted()) {
            throw new UsernameNotFoundException("탈퇴한 회원입니다: " + nickname);
        }

        return new UserDetailsImpl(user);
    }
}
