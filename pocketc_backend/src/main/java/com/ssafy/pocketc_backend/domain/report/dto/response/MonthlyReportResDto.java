package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.util.List;

/**
 * 통계 페이지에 전달할 패키지
 * @param budget: 당월 예산
 * @param currMonthExpense: 현재 달의 총지출액
 * @param lastMonthExpense: 지난 달의 총지출액
 * @param expenseByCategories: 카테고리별 지출액
 */
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
