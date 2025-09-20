package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.util.List;

public record MonthlyReportResDto(
        Long budget,
        Long currMonthExpense,
        Long lastMonthExpense,
        List<ExpenseByCategoryDto> expenseByCategories,
        List<MonthlySavingScoreDto> monthlySavingScoreList
) {
    public static MonthlyReportResDto of(Long budget, Long currMonthExpense, Long lastMonthExpense, List<ExpenseByCategoryDto> expenseByCategories, List<MonthlySavingScoreDto> monthlySavingScoreList) {
        return new MonthlyReportResDto(budget, currMonthExpense, lastMonthExpense, expenseByCategories, monthlySavingScoreList);
    }
}
