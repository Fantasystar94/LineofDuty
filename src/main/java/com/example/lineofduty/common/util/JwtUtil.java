package com.example.lineofduty.common.util;

import com.example.lineofduty.common.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";
    public static final long TOKEN_TIME = 60 * 60 * 1000L;  // 1시간

    @Value("${JWT_SECRET}")
    private String jwtSecretKey;
    private SecretKey secretKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecretKey);
        this.secretKey = Keys.hmacShaKeyFor(bytes);
        this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    // 토큰 생성
    public String generateToken(Long userId, Role userRole) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("userRole", userRole)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TOKEN_TIME))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("유효하지 않는 JWT 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 복호화
    public Claims extractAllClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public Long extractUserId(String token) {
        return Long.valueOf(extractAllClaims(token).getSubject());
    }

    public Role extractUserRole(String token) {
        String role = extractAllClaims(token).get("userRole", String.class);
        return Role.valueOf(role);
    }

}
