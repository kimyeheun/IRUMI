package com.ssafy.pocketc_backend.domain.transaction.dto.response;

public record TransactionCreatedResDto(
        Integer transactionId,
        Integer userId
) {
    public static TransactionCreatedResDto of(Integer transactionId, Integer userId) {
        return new TransactionCreatedResDto(transactionId, userId);
    }
}
