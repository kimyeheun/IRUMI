package com.ssafy.pocketc_backend.domain.mission.dto.request;

import java.time.LocalDateTime;

public record MissionReqDto(
        Integer missionId,
        String mission,
        Integer subId,
        String dsl,
        Integer type,
        LocalDateTime validFrom,
        LocalDateTime validTo
) {
    public static MissionReqDto of(Integer missionId, String mission, Integer subId, String dsl, Integer type, LocalDateTime validFrom, LocalDateTime validTo) {
        return new MissionReqDto(missionId, mission, subId, dsl, type, validFrom, validTo);
    }
}