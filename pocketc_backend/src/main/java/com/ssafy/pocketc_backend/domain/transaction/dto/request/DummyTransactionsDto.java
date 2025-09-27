package com.ssafy.pocketc_backend.domain.transaction.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DummyTransactionsDto {
    @JsonProperty("transactions")
    private List<Dummy> transactions;

    public DummyTransactionsDto() {}
    public DummyTransactionsDto(List<Dummy> transactions) {
        this.transactions = transactions;
    }
    public List<Dummy> getTransactions() { return transactions; }
    public void setTransactions(List<Dummy> transactions) { this.transactions = transactions; }
}