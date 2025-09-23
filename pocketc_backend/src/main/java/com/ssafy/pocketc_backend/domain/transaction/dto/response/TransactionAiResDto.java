package com.ssafy.pocketc_backend.domain.transaction.dto.response;

import java.time.LocalDateTime;

public record TransactionAiResDto(
        Integer majorId,
        Integer subId,
        Long amount,
        boolean isFixed,
        String merchantName,
        LocalDateTime transactedAt
) {}