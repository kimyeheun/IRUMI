package com.ssafy.pocketc_backend.domain.report.dto.response;

/**
 * 카테고리별 월간 지출액
 * @param categoryId: 카테고리의 ID
 * @param expense: 카테고리의 월간 지출액
 */
public record ExpenseByCategoryDto(
        Integer categoryId,
        Long expense
) {
    public static ExpenseByCategoryDto of(Integer categoryId, Long expense) {
        return new ExpenseByCategoryDto(categoryId, expense);
    }
}
