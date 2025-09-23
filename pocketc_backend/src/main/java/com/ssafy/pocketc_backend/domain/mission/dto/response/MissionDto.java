package com.ssafy.pocketc_backend.domain.mission.dto.response;

import com.ssafy.pocketc_backend.domain.mission.entity.Mission;

public record MissionDto(
        Integer subId,
        Integer type,
        String mission,
        String status
) {
    public static MissionDto from(Mission mission) {
        return new MissionDto(
                mission.getSubId(),
                mission.getType(),
                mission.getMission(),
                String.valueOf(mission.getStatus())
        );
    }
}