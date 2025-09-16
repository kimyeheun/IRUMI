package com.ssafy.pocketc_backend.global.auth;

import com.ssafy.pocketc_backend.domain.user.dto.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
//토큰 발급 테스트용 임시코드,나중에 삭제 예정
@Component
@Profile("!test")
@RequiredArgsConstructor
public class JwtTestRunner implements CommandLineRunner {

    private final JwtProvider jwtProvider;

    @Override
    public void run(String... args) {
        Integer userId = 123;
        String email = "test@example.com";

        // 1. 토큰 발급
        UserLoginResponse tokens = jwtProvider.issueToken(userId, email);
        System.out.println("AccessToken: " + tokens.accessToken());
        System.out.println("RefreshToken: " + tokens.refreshToken());

        // 2. AccessToken 검증
        boolean isAccessValid = jwtProvider.validateAccessToken(tokens.accessToken());
        System.out.println("AccessToken valid? " + isAccessValid);

        // 3. userId 추출
        Integer parsedId = jwtProvider.getUserIdFromJwt(tokens.refreshToken());
        System.out.println("Parsed userId: " + parsedId);

        // 4. RefreshToken 검증
        Integer refreshParsedId = jwtProvider.validateRefreshToken(tokens.refreshToken());
        System.out.println("RefreshToken valid for userId: " + refreshParsedId);
    }
}
