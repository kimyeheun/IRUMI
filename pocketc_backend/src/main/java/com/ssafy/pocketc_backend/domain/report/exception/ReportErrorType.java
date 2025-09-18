package com.ssafy.pocketc_backend.domain.report.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReportErrorType implements ErrorType {
    ERROR_GET_MONTHLY_TOTAL_EXPENSE(HttpStatus.BAD_REQUEST, "해당 월의 총액을 가져올 수 없습니다."),
    ERROR_GET_MONTHLY_BUDGET_AND_TOTAL_EXPENSE(HttpStatus.NOT_FOUND, "해당 월의 예산과 총액을 가져올 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
