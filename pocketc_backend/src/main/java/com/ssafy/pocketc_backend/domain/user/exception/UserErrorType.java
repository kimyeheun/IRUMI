package com.ssafy.pocketc_backend.domain.user.exception;

import com.ssafy.pocketc_backend.global.exception.type.ErrorType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public enum UserErrorType implements ErrorType {
    NOT_FOUND_MEMBER_ERROR(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_MATCHED_MEMBER(HttpStatus.FORBIDDEN,"회원정보가 일치하지 않습니다"),
    S3_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자 입니다");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }

}