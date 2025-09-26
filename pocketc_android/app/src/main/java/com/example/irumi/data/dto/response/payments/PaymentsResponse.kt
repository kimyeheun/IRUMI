package com.example.irumi.data.dto.response.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsResponse(
    val transactions: List<Payment>,
    val totalSpending: Int
)

@Serializable
data class Payment(
    val transactionId: Int,
    val transactedAt: String,
    val amount: Int,
    val majorId: Int,
    val subId: Int,
    val merchantName: String,
    val isApplied: Boolean,
    val isFixed: Boolean,
    val createdAt: String,
    val updatedAt: String
)