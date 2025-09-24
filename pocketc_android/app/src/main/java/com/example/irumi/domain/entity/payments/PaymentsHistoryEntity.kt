package com.example.irumi.domain.entity.payments

data class PaymentsHistoryEntity(
    val payments: List<PaymentEntity>,
    val totalSpending: Int
)
