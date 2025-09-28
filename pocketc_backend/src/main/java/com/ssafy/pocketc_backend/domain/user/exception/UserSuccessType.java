package com.ssafy.pocketc_backend.domain.user.exception;

import com.ssafy.pocketc_backend.global.exception.type.SuccessType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public enum UserSuccessType implements SuccessType {
    PROCESS_SUCCESS(HttpStatus.OK, "OK"),

    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.NO_CONTENT, "로그아웃에 성공했습니다."),
    UPDATE_MEMBER_SUCCESS(HttpStatus.OK, "회원 정보 수정이 완료되었습니다"),
    SIGNUP_MEMBER_SUCCESS(HttpStatus.OK, "회원가입이 완료되었습니다"),
    WITHDRAW_MEMBER_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴가 완료되었습니다."),

    GET_ME_SUCCESS(HttpStatus.OK, "회원 조회에 성공했습니다."),
    UPLOAD_SUCCESS(HttpStatus.OK, "이미지 업로드에 성공했습니다."),
    SUCCESS_GET_CODE(HttpStatus.OK, "회원 코드 조회 성공"),

    SUCCESS_UPDATE_TRANSACTIONS(HttpStatus.OK, "거래내역 AI 성공");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}