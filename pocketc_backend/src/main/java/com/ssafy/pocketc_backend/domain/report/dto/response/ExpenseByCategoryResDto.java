package com.ssafy.pocketc_backend.domain.report.dto.response;

/**
 * 카테고리별 월간 지출액
 * @param categoryId: 카테고리의 ID
 * @param expense: 카테고리의 월간 지출액
 */
public record ExpenseByCategoryResDto(
        Integer categoryId,
        Integer expense
) {
    public static ExpenseByCategoryResDto of(Integer categoryId, Integer expense) {
        return new ExpenseByCategoryResDto(categoryId, expense);
    }
}
