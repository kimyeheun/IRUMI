package com.ssafy.pocketc_backend.domain.transaction.dto.response;

import java.util.List;

public record TransactionListResDto(
        List<TransactionResDto> transactions,
        Integer totalSpending
) {
    public static TransactionListResDto of(List<TransactionResDto> transactions, Integer totalSpending) {
        return new TransactionListResDto(transactions, totalSpending);
    }
}