package com._plus1.common.security;

import com._plus1.common.exception.CustomException;
import com._plus1.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey secretKey;

    /**
     * 시크릿키 준비
     */
    @PostConstruct
    void init() {
        byte[] decodedKeyByteList = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(decodedKeyByteList);
    }

    /**
     * 토큰 발급
     */
    public String createJwt(String nickname) {

        // 데이터 준비
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60); // 만료시간: 60분

        // 토큰 만들기
        String jwt = Jwts.builder()
                .subject(nickname)
                .expiration(expiration)
                .issuedAt(now)
                .signWith(secretKey)
                .compact();

        return jwt;
    }

    /**
     * 토큰 검증
     */
    public boolean verifyToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.info("[Invalid JWT signature] 유효하지 않은 JWT 서명 입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("[Expired JWT token] 만료된 JWT token 입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.error("[Unsupported JWT token] 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("[JWT claims is empty] 잘못된 JWT 토큰 입니다.");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 헤더에서 토큰 추출 (Bearer 제거)
     */
    public String getJwtFromHeader(jakarta.servlet.http.HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (org.springframework.util.StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " 문자열 제외한 토큰만 반환
        }
        return null;
    }

    /**
     * 토큰에서 닉네임(Subject) 추출
     */
    public String getUserInfoFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // createJwt에서 넣은 nickname 반환
    }
}
