package com.ssafy.pocketc_backend.domain.mission.dto.response;

public record MissionInfoDto(
        boolean check,
        Long progress,
        Long value,
        String template
) {
    public static MissionInfoDto of(boolean check, Long progress, Long value, String template) {
        return new MissionInfoDto(check, progress, value, template);
    }
}