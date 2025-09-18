package com.ssafy.pocketc_backend.domain.event.dto.response;

public record MemberDto(
        Integer userId,
        String name,
        String profileImageUrl
) {
    public static MemberDto of(Integer userId, String name, String profileImageUrl) {
        return new MemberDto(userId, name, profileImageUrl);
    }
}