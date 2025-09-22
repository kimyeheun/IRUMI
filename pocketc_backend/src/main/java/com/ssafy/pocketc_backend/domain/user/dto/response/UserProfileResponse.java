package com.ssafy.pocketc_backend.domain.user.dto.response;

public record UserProfileResponse(Integer userId, String name, Long budget, String profileImageUrl) {
}
