package com.ssafy.pocketc_backend.domain.event.dto.response;

public record MemberDto(
        Integer userId,
        String name,
        String profileImageUrl,
        Boolean isFriend
) {
    public static MemberDto of(Integer userId, String name, String profileImageUrl, Boolean isFriend) {
        return new MemberDto(userId, name, profileImageUrl, isFriend);
    }
}