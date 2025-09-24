package com.ssafy.pocketc_backend.domain.mission.dto.request;

import java.time.LocalDateTime;

public record MissionItem(
        String mission,
        Integer subId,
        String dsl,
        Integer type,
        LocalDateTime validFrom,
        LocalDateTime validTo
) {}