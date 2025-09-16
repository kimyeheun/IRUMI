package com.ssafy.pocketc_backend.domain.user.dto.response;

public record UserLoginResponse(String accessToken, String refreshToken) {
}