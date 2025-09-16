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
    TEAM_MEMBER_REMOVE_SUCCESS(HttpStatus.OK, "팀원 내보내기가 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}