package com.ssafy.pocketc_backend.domain.mission.dto.response;

public record MissionDto(
        Integer subId,
        Integer type,
        String mission,
        String status,
        Long progress
) {
    public static MissionDto of(Integer subId, Integer type, String mission, String status, Long progress) {
        return new MissionDto(subId, type, mission, status, progress);
    }
}