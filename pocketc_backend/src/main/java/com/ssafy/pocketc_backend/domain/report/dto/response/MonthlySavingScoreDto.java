package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.time.LocalDate;

public record MonthlySavingScoreDto(
        LocalDate month,
        Integer savingScore
) {
    public static MonthlySavingScoreDto of(LocalDate month, Integer savingScore) {
        return new MonthlySavingScoreDto(month, savingScore);
    }
}
