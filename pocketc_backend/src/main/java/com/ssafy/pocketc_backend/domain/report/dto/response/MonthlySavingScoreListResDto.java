package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MonthlySavingScoreListResDto(
        LocalDate month,
        Integer savingScore
) {
    public static MonthlySavingScoreListResDto of(LocalDate month, Integer savingScore) {
        return new MonthlySavingScoreListResDto(month, savingScore);
    }
}
