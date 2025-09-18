package com.ssafy.pocketc_backend.domain.transaction.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TransactionSuccessType implements SuccessType {
    SUCCESS_GET_TRANSACTION(HttpStatus.OK, "결제내역 조회 성공"),
    SUCCESS_GET_MONTHLY_TRANSACTIONS(HttpStatus.OK, "월별 결제내역 리스트 조회 성공"),
    SUCCESS_UPDATE_TRANSACTION(HttpStatus.CREATED, "결제내역 수정 성공"),
    SUCCESS_GET_MAJOR_CATEGORY_TRANSACTIONS(HttpStatus.OK, "대분류 결제내역 리스트 조회 성공"),
    SUCCESS_GET_SUB_CATEGORY_TRANSACTIONS(HttpStatus.OK, "소분류 결제내역 리스트 조회 성공"),
    SUCCESS_CREATE_TRANSACTIONS(HttpStatus.CREATED, "결제내역 생성 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}