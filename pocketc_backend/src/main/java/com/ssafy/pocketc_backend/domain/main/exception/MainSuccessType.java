package com.ssafy.pocketc_backend.domain.main.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public enum MainSuccessType implements SuccessType {
    SUCCESS_GET_STREAKS(HttpStatus.OK, "스트릭 조회 완료"),
    SUCCESS_GET_DAILY_STAT(HttpStatus.OK, "하루 지출 및 절약점수 조회 완료");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}