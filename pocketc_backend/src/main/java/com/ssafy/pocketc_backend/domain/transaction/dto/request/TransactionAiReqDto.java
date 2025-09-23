package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record TransactionAiReqDto(
        String transactedAt,
        Long amount,
        String merchantName
) {
    public static TransactionAiReqDto of(String transactedAt, Long amount, String merchantName) {
        return new TransactionAiReqDto(transactedAt, amount, merchantName);
    }
}