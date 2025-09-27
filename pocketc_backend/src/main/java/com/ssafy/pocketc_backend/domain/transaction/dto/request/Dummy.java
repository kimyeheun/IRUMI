// Dummy.java
package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Dummy {
    @JsonProperty("transactionId")
    private Integer transactionId;

    @JsonProperty("transactedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactedAt;

    @JsonProperty("majorId")
    private Integer majorId;

    @JsonProperty("subId")
    private Integer subId;

    @JsonProperty("merchantName")
    private String merchantName;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("isFixed")
    private boolean isFixed;

    public Dummy() {} // 기본 생성자

    public Dummy(Integer transactionId, LocalDateTime transactedAt, Integer majorId, Integer subId,
                 String merchantName, Long amount, boolean isFixed) {
        this.transactionId = transactionId;
        this.transactedAt = transactedAt;
        this.majorId = majorId;
        this.subId = subId;
        this.merchantName = merchantName;
        this.amount = amount;
        this.isFixed = isFixed;
    }
}