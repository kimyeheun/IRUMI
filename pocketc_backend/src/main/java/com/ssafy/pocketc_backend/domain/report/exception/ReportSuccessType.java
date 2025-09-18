package com.ssafy.pocketc_backend.domain.report.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReportSuccessType implements SuccessType {
    SUCCESS_GET_MONTHLY_TOTAL_EXPENSE(HttpStatus.OK, "월간 예산, 총지출액 조회 성공"),
    SUCCESS_GET_EXPENSE_COMPARISON(HttpStatus.OK, "전월 대비 금액 조회 성공"),
    SUCCESS_GET_EXPENSE_BY_CATEGORY(HttpStatus.OK, "카테고리별 지출액 조회 성공"),
    SUCCESS_GET_MONTHLY_SAVING_SCORE_LIST(HttpStatus.OK, "월별 절약점수 추이 조회 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() { return httpStatus.value(); }
}
