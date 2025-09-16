package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.YearMonth;

public record MonthReqDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
        YearMonth month
) {}