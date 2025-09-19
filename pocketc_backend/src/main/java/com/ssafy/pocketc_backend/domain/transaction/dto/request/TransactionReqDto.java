package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record TransactionReqDto(
        LocalDateTime date,
        Long amount,
        Integer majorCategory,
        Integer subCategory,
        String merchantName,
        boolean isFixed
) {}