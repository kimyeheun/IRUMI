package com.ssafy.pocketc_backend.domain.main.dto;

import com.ssafy.pocketc_backend.domain.user.entity.Streak;

import java.time.LocalDate;

public record StreakDto(
        LocalDate date,
        Integer missionsCompleted,
        Long spending,
        Boolean isActive
) {
    public static StreakDto from(Streak streak) {
        return new StreakDto(streak.getDate(), streak.getMissionCompletedCount(), streak.getSpentAmount(), streak.isStatus());
    }
}
