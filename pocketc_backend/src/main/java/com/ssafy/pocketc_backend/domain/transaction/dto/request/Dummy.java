package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record Dummy(
        Integer transactionId,
        LocalDateTime date,
        Long amount,
        Boolean isFixed,
        String merchantName,
        Integer subId,
        Integer majorId
) {
    public static Dummy of(Integer transactionId, LocalDateTime date, Long amount, Boolean isFixed, String merchantName, Integer subId, Integer majorId) {
        return new Dummy(transactionId, date, amount, isFixed, merchantName, subId, majorId);
    }
}