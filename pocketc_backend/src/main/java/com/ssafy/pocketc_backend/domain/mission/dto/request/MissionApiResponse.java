package com.ssafy.pocketc_backend.domain.mission.dto.request;

public record MissionApiResponse(
        int status,
        String message,
        MissionData data
) {}

