package com.ssafy.pocketc_backend.domain.report.dto.response;

/**
 * 전월 대비 증가 감소를 구하기 위한 Response DTO
 * @param lastMonthExpense: 지난달의 월간 지출액
 * @param currentMonthExpense: 이번달의 월간 지출액
 */
public record ExpenseComparisonResDto(
        Integer lastMonthExpense,
        Integer currentMonthExpense
) {
    public static ExpenseComparisonResDto of(Integer lastMonthExpense, Integer currentMonthExpense) {
        return new ExpenseComparisonResDto(lastMonthExpense, currentMonthExpense);
    }
}
