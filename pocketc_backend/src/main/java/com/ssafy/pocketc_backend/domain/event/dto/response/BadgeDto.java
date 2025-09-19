package com.ssafy.pocketc_backend.domain.event.dto.response;

import java.time.LocalDateTime;

public record BadgeDto(
        Integer badgeId,
        String badgeName,
        String badgeDescription,
        Integer level,
        String badgeImageUrl,
        LocalDateTime createdAt
) {
    public static BadgeDto of(Integer badgeId, String badgeName, String badgeDescription, Integer level, String badgeImageUrl, LocalDateTime createdAt) {
        return new BadgeDto(badgeId, badgeName, badgeDescription, level, badgeImageUrl, createdAt);
    }
}