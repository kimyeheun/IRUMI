package com.ssafy.pocketc_backend.global.auth.jwt;

import com.ssafy.pocketc_backend.domain.user.dto.response.UserLoginResponse;
import com.ssafy.pocketc_backend.global.auth.service.RefreshTokenService;
import com.ssafy.pocketc_backend.global.dev.DevTokenHolder;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static com.ssafy.pocketc_backend.global.exception.type.GlobalErrorType.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    private SecretKey signingKey;

    private final RefreshTokenService refreshTokenService; // Redis 기반 서비스

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 14;//임시 2주(30분)
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 14;//2주

    @PostConstruct
    private void initKey() {
        if (JWT_SECRET == null || JWT_SECRET.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not configured!");
        }
        // JWT_SECRET은 Base64로 인코딩됨, 실제 암호화에 사용되는 키는 디코딩되어야 함.
        byte[] decodedKey = Base64.getDecoder().decode(JWT_SECRET);
        this.signingKey = Keys.hmacShaKeyFor(decodedKey);
    }
    // Access Token, Refresh Token을 발급하는 메서드
    // TODO: 이 메서드는 비즈니스 로직 (Redis 저장 포함)이므로 나중에 UserService로 옮길것
    public UserLoginResponse issueToken(Integer userId, String email) {
        String accessToken = generateJwt(userId, email, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = generateJwt(userId, email, REFRESH_TOKEN_EXPIRATION_TIME);

        //Redis에 저장
        refreshTokenService.save(userId.toString(), refreshToken);

        return new UserLoginResponse(accessToken, refreshToken);
    }

    public String generateJwt(Integer userId, String email, long expirationTime) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        if (token == null || token.isBlank()) {
            throw new CustomException(EMPTY_JWT_TOKEN);
        }
        //개발자용 고정 토큰 무조건 허용
        if (DevTokenHolder.DEV_TOKENS.containsValue(token)) {
            return true;
        }
        try {
            getJwtBody(token);
            return true;
        } catch (MalformedJwtException ex) { // JWT 구조 이상 및 손상
            throw new CustomException(INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException ex) { // 유효기간 만료
            throw new CustomException(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException ex) { //지원하지 않는 JWT 형식
            throw new CustomException(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException ex) { // null 또는 ""
            throw new CustomException(EMPTY_JWT_TOKEN);
        } catch (SecurityException ex) { // JWT 서명 검증 실패
            throw new CustomException(INVALID_JWT_SIGNATURE);
        } catch (Exception e) { // 등등
            throw new CustomException(UNKNOWN_JWT_ERROR);
        }
    }

    // TODO: 이 메서드도 Redis 검증 포함 → UserService로 옮기고, JwtProvider에는 "토큰 파싱만" 남겨두는 게 좋음
    public Integer validateRefreshToken(String refreshToken) {
        Claims claims = getJwtBody(refreshToken);
        Integer userId = Integer.parseInt(claims.getSubject());

        boolean isValid = refreshTokenService.validate(userId.toString(), refreshToken);
        if (!isValid) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }

        return userId;
    }


    public Integer getUserIdFromJwt(String token) {
        for (var entry : DevTokenHolder.DEV_TOKENS.entrySet()) {
            if (entry.getValue().equals(token)) {
                return entry.getKey();
            }
        }
        Claims claims = getJwtBody(token);
        return Integer.parseInt(claims.getSubject());
    }

    private Claims getJwtBody(String token) {
        if (token == null || token.isBlank()) {
            throw new CustomException(EMPTY_JWT_TOKEN);
        }
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // TODO: 이 메서드도 비즈니스 로직 (재발급 + Redis 갱신) → UserService로 옮기는 게 맞음
    public UserLoginResponse reissueToken(String refreshToken) {
        Integer userId = validateRefreshToken(refreshToken);

        // 이메일은 claim에서 꺼낼 수 있음
        Claims claims = getJwtBody(refreshToken);
        String email = claims.get("email", String.class);

        String newAccessToken = generateJwt(userId, email, ACCESS_TOKEN_EXPIRATION_TIME);
        String newRefreshToken = generateJwt(userId, email, REFRESH_TOKEN_EXPIRATION_TIME);

        // Redis 갱신
        refreshTokenService.save(userId.toString(), newRefreshToken);

        return new UserLoginResponse(newAccessToken, newRefreshToken);
    }

}
