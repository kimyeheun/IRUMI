package com.ssafy.pocketc_backend.domain.transaction.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TransactionSuccessType implements SuccessType {
    SUCCESS_GET_TRANSACTION(HttpStatus.OK, "결제 내역 조회 성공"),
    SUCCESS_GET_MONTHLY_TRANSACTIONS(HttpStatus.OK, "월별 결제 내역 리스트 조회 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}