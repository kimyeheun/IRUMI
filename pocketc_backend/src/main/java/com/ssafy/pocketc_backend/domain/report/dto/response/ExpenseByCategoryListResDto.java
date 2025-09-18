package com.ssafy.pocketc_backend.domain.report.dto.response;

import java.util.List;

/**
 * 카테고리별 지출액 리스트 Response DTO
 * @param expenseByCategories: 카테고리 정보, 카테고리별 월간 지출액 리스트
 */
public record ExpenseByCategoryListResDto(
        List<ExpenseByCategoryResDto> expenseByCategories
) {
    public static ExpenseByCategoryListResDto of(List<ExpenseByCategoryResDto> expenseByCategories) {
        return new ExpenseByCategoryListResDto(expenseByCategories);
    }
}
