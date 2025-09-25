package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record Dummy(
        Integer userId,
        LocalDateTime date,
        Long amount,
        String merchantName,
        Integer subId,
        Integer majorId
) {
    public static Dummy of(Integer userId, LocalDateTime date, Long amount, String merchantName, Integer majorId, Integer subId) {
        return new Dummy(userId, date, amount, merchantName, majorId, subId);
    }
}