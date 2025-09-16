package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.time.LocalDateTime;

public record TransactionReqDto(
        LocalDateTime date,
        Integer amount,
        Integer majorCategory,
        Integer subCategory,
        String merchantName,
        boolean isFixed
) {}