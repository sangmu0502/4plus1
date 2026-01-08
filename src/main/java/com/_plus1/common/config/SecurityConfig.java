package com._plus1.common.config;

import com._plus1.common.security.JwtAuthorizationFilter;
import com._plus1.common.security.JwtUtil;
import com._plus1.domain.user.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        // 세션을 사용하지 않음 (JWT 기반 인증이므로 STATELESS 모드 설정)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그인, 회원가입은 인증 없이 허용. 그 외의 모든 요청은 인증 필요
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/me/signup", "/api/albums/**", "/api/search/**", "/api/songs/top", "/api/songs/top/v2", "/api/songs/korea/new", "/api/songs/global/new", "/api/songs/korea", "/api/songs/global").permitAll() // 로그인, 회원가입은 인증 없이 허용
                .anyRequest().permitAll()
        );

        // JWT 인가 필터 추가 (UsernamePasswordAuthenticationFilter 이전에 실행되도록)
        http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
