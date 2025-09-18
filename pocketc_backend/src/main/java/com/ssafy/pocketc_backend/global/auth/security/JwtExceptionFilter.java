package com.ssafy.pocketc_backend.global.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.ssafy.pocketc_backend.global.exception.type.GlobalErrorType.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            // JwtAuthenticationFilter 에서 예외 발생 시 핸들링
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            if (e.getErrorType().equals(INVALID_JWT_TOKEN)) {
                setErrorResponse(response, INVALID_JWT_TOKEN);
            } else if (e.getErrorType().equals(EXPIRED_JWT_TOKEN)) {
                setErrorResponse(response, EXPIRED_JWT_TOKEN);
            } else if (e.getErrorType().equals(UNSUPPORTED_JWT_TOKEN)) {
                setErrorResponse(response, UNSUPPORTED_JWT_TOKEN);
            } else if (e.getErrorType().equals(EMPTY_JWT_TOKEN)) {
                setErrorResponse(response, EMPTY_JWT_TOKEN);
            } else if (e.getErrorType().equals(INVALID_JWT_SIGNATURE)) {
                setErrorResponse(response, INVALID_JWT_SIGNATURE);
            } else if (e.getErrorType().equals(UNKNOWN_JWT_ERROR)) {
                setErrorResponse(response, UNKNOWN_JWT_ERROR);
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorType errorType) {
        response.setStatus(errorType.getHttpStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorType.getHttpStatusCode(), errorType.getMessage());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Data
    public static class ErrorResponse {
        private final Integer code;
        private final String message;
    }
}