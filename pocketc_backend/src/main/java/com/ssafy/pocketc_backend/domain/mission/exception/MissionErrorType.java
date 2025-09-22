package com.ssafy.pocketc_backend.domain.mission.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MissionErrorType implements ErrorType {
    ERROR_GET_MISSIONS(HttpStatus.BAD_REQUEST, "미션 조회 오류");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}