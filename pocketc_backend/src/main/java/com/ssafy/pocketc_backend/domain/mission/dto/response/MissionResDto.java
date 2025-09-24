package com.ssafy.pocketc_backend.domain.mission.dto.response;

import java.util.List;

public record MissionResDto(
        Boolean missionReceived,
        List<MissionDto> missions
) {}