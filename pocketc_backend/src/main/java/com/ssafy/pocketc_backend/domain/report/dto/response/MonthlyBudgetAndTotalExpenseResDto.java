package com.ssafy.pocketc_backend.domain.report.dto.response;

/**
 * 당월 예산, 월간 총지출액 Response DTO
 * @param budget: 예산
 * @param monthlyTotalExpense: 월간 총지출액
 */
public record MonthlyBudgetAndTotalExpenseResDto(
    Integer budget,
    Integer monthlyTotalExpense
) {
    public static MonthlyBudgetAndTotalExpenseResDto of(Integer budget, Integer monthlyTotalExpense) {
        return new MonthlyBudgetAndTotalExpenseResDto(budget, monthlyTotalExpense);
    }
}
