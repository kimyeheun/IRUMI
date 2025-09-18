package com.ssafy.pocketc_backend.domain.follow.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FollowErrorType implements ErrorType {
    ERROR_POST_SELF_FOLLOW(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우 할 수는 없습니다."),
    ERROR_GET_NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() { return httpStatus.value(); }
}
