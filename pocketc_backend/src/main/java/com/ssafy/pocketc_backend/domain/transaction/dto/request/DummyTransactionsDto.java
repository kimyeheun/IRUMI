package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import java.util.List;

public record DummyTransactionsDto(List<TransactionCreateReqDto> transactions) {}