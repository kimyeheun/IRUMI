package com.ssafy.pocketc_backend.domain.transaction.dto.request;

public record TransactionReqDto(
        Long amount,
        Integer majorId,
        Integer subId,
        String merchantName,
        Boolean isFixed
) {}