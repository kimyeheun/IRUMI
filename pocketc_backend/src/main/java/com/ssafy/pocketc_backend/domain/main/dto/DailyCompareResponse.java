package com.ssafy.pocketc_backend.domain.main.dto;

public record DailyCompareResponse(
    MainResponse me,
    MainResponse friend
) {}
