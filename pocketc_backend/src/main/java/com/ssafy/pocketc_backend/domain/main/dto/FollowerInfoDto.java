package com.ssafy.pocketc_backend.domain.main.dto;

import com.ssafy.pocketc_backend.domain.event.dto.response.BadgeDto;

import java.util.List;

public record FollowerInfoDto(
        Integer followerId,
        String followerName,
        Long mySavingScore,
        Long followerSavingScore,
        List<BadgeDto> badges,
        List<StreakDto> streaks
) {
    public static FollowerInfoDto of(
            Integer followerId,
            String followerName,
            Long mySavingScore,
            Long followerSavingScore,
            List<BadgeDto> badges,
            List<StreakDto> streaks
    ) {
        return new FollowerInfoDto(followerId, followerName, mySavingScore, followerSavingScore, badges, streaks);
    }
}