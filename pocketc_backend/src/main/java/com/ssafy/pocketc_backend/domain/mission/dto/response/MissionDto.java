package com.ssafy.pocketc_backend.domain.mission.dto.response;

import com.ssafy.pocketc_backend.domain.mission.entity.Mission;

public record MissionDto(
        Integer missionId,
        Integer subId,
        String timeTag,
        String mission,
        String status
) {
    public static MissionDto from(Mission mission) {
        return new MissionDto(
                mission.getMissionId(),
                mission.getSubId(),
                java.lang.String.valueOf(mission.getTimeTag()),
                mission.getMission(),
                java.lang.String.valueOf(mission.getSubId())
        );
    }
}