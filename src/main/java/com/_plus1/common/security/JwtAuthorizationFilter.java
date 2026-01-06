package com._plus1.common.security;

import com._plus1.domain.user.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 꺼내기
        String token = jwtUtil.getJwtFromHeader(request);

        if (token != null) {
            // 토큰 검증
            if (jwtUtil.verifyToken(token)) {
                // 토큰에서 유저 정보(nickname) 추출
                String nickname = jwtUtil.getUserInfoFromToken(token);

                // 인증 객체 생성 및 Context에 저장
                UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    // 로그인 및 회원가입 경로는 토큰 검사 로직을 타지 않고 바로 다음 필터로 넘김
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        String path = request.getRequestURI();
        return path.startsWith("/api/auth")
                || path.equals("/api/me/signup");
    }
}