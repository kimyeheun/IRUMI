package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.time.LocalDate;

public record MonthlySavingScoreDto(
        LocalDate month,
        Double savingScore
) {
    public static MonthlySavingScoreDto of(LocalDate month, Double savingScore) {
        return new MonthlySavingScoreDto(month, savingScore);
    }
}
