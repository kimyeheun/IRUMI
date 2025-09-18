package com.ssafy.pocketc_backend.global.auth.security;

import com.ssafy.pocketc_backend.global.auth.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ssafy.pocketc_backend.global.auth.security.AuthWhiteList.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        /*
         * 인증/인가 필요없는 uri 처리
         */
        if (AUTH_WHITELIST_DEFAULT.stream()
                .anyMatch(whiteUrl -> pathMatcher.match(whiteUrl, request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (AUTH_WHITELIST_WILDCARD.stream()
                .anyMatch(whiteUrl -> pathMatcher.match(whiteUrl, request.getRequestURI()))) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Jwt Token을 얻고, 유효성 검사.
         * 문제가 있을 경우 예외 발생. 없을 경우 authentication 설정 후 필터 수행
         */
        final String token = getJwtFromRequest(request);
        if (jwtProvider.validateAccessToken(token)) {
            Integer userId = jwtProvider.getUserIdFromJwt(token);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
