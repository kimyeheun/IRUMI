package com.ssafy.pocketc_backend.domain.transaction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.YearMonth;
import java.util.List;

public record TransactionListResDto(
        @JsonFormat(pattern = "yyyy-MM")
        YearMonth month,
        List<TransactionResDto> transactions,
        Integer totalSpending
) {
    public static TransactionListResDto of(YearMonth month, List<TransactionResDto> transactions, Integer totalSpending) {
        return new TransactionListResDto(month, transactions, totalSpending);
    }
}