package com.ssafy.pocketc_backend.domain.transaction.dto.response;

import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;

import java.time.LocalDateTime;

public record TransactionResDto(
        Integer transactionId,
        LocalDateTime transactedAt,
        Long amount,
        Integer majorId,
        Integer subId,
        String merchantName,
        Boolean isFixed,
        Boolean isApplied,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResDto from(Transaction transaction) {
        return new TransactionResDto(
                transaction.getTransactionId(),
                transaction.getTransactedAt(),
                transaction.getAmount(),
                transaction.getMajorId(),
                transaction.getSubId(),
                transaction.getMerchantName(),
                transaction.isFixed(),
                transaction.isApplied(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }
}