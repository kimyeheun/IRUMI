package com.ssafy.pocketc_backend.domain.event.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EventSuccessType implements SuccessType {
    SUCCESS_GET_ROOM(HttpStatus.OK, "방 조회 성공"),
    SUCCESS_JOIN_ROOM(HttpStatus.CREATED, "방 입장 성공"),
    SUCCESS_CREATE_ROOM(HttpStatus.CREATED, "방 생성 성공"),
    SUCCESS_LEAVE_ROOM(HttpStatus.OK, "방 퇴장 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}