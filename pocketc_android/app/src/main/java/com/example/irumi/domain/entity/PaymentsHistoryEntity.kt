package com.example.irumi.domain.entity

data class PaymentsHistoryEntity(
    val payments: List<PaymentEntity>,
    val totalSpending: Int
)
