package com.ssafy.pocketc_backend.domain.mission.dto.response;

public record MissionDto(
        Integer missionId,
        Integer subId,
        Integer type,
        String mission,
        String status,
        Long progress,
        Long value,
        String template
) {
    public static MissionDto of(Integer missionId, Integer subId, Integer type, String mission, String status, Long progress, Long value, String template) {
        return new MissionDto(missionId, subId, type, mission, status, progress, value, template);
    }
}