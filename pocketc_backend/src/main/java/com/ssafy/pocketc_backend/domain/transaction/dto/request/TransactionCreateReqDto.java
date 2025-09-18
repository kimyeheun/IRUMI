package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record TransactionCreateReqDto(
        Integer userId,
        LocalDateTime date,
        Integer amount,
        String merchantName
) {
    public static TransactionCreateReqDto of(Integer userId, LocalDateTime date, Integer amount, String merchantName) {
        return new TransactionCreateReqDto(userId, date, amount, merchantName);
    }
}
