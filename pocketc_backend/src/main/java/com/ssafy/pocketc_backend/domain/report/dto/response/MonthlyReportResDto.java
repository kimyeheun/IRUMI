package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.util.List;

public record MonthlyReportResDto(
        Long budget,

        List<ExpenseByCategoryDto> expenseByCategoryDtos,
        MonthlySavingScoreDto monthlySavingScoreDto
) {
}
