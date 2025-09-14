package com.ssafy.pocketc_backend.global.exception.type;

public interface ErrorType {
    int getHttpStatusCode();

    String getMessage();
}
