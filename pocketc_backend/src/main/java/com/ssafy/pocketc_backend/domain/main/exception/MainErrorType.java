package com.ssafy.pocketc_backend.domain.main.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public enum MainErrorType implements ErrorType {
    ERROR_GET_STREAKS(HttpStatus.NOT_FOUND, "스트릭 조회 실패");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }

}