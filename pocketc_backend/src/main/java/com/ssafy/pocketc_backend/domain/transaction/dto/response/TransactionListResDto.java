package com.ssafy.pocketc_backend.domain.transaction.dto.response;

import java.util.List;

public record TransactionListResDto(
        List<TransactionResDto> transactions,
        Long totalSpending
) {
    public static TransactionListResDto of(List<TransactionResDto> transactions, Long totalSpending) {
        return new TransactionListResDto(transactions, totalSpending);
    }
}