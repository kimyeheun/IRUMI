package com.ssafy.pocketc_backend.domain.transaction.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TransactionErrorType implements ErrorType {
    ERROR_GET_TRANSACTION(HttpStatus.BAD_REQUEST, "거래내역을 찾을 수 없습니다."),
    ERROR_GET_MONTHLY_TRANSACTIONS(HttpStatus.BAD_REQUEST, "해당 월 거래내역을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}