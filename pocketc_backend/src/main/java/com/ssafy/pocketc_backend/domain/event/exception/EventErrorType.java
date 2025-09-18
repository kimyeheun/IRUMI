package com.ssafy.pocketc_backend.domain.event.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EventErrorType implements ErrorType {
    ERROR_GET_TRANSACTION(HttpStatus.BAD_REQUEST, "거래내역을 찾을 수 없습니다."),
    ERROR_GET_MONTHLY_TRANSACTIONS(HttpStatus.BAD_REQUEST, "해당 월 거래내역을 찾을 수 없습니다."),
    ERROR_UPDATE_TRANSACTION(HttpStatus.BAD_REQUEST, "거래내역 수정 실패"),
    ERROR_GET_ROOM(HttpStatus.BAD_REQUEST, "해당 방을 찾을 수 없습니다."),
    ERROR_ALREADY_INCLUDED_ROOM(HttpStatus.BAD_REQUEST, "이미 방에 속해져 있습니다."),
    ERROR_NOT_INCLUDED_ROOM(HttpStatus.BAD_REQUEST, "방에 속해져 있지 않습니다."),
    ERROR_MAX_PUZZLES(HttpStatus.BAD_REQUEST, "퍼즐 갯수가 최대입니다."),
    ERROR_MAX_MEMBERS(HttpStatus.BAD_REQUEST, "최대 멤버 수에 도달하였습니다."),
    ERROR_NO_PUZZLE_ATTEMPTS(HttpStatus.BAD_REQUEST, "퍼즐 조각이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}