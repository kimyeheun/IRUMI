package com.ssafy.pocketc_backend.domain.follow.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FollowSuccessType implements SuccessType {
    SUCCESS_GET_FOLLOWS(HttpStatus.OK, "팔로우 리스트 조회 성공"),
    SUCCESS_POST_FOLLOW(HttpStatus.OK, "팔로우 성공"),
    SUCCESS_DELETE_FOLLOW(HttpStatus.OK, "언팔로우 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
