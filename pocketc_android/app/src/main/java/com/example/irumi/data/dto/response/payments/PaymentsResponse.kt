package com.example.irumi.data.dto.response.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentsResponse(
    val payments: List<Payment>,
    val totalSpending: Int
)

@Serializable
data class Payment(
    val transactionId: Int,
    val date: String,
    val amount: Int,
    val majorCategory: Int,
    val subCategory: Int,
    val merchantName: String,
    val isApplied: Boolean,
    val isFixed: Boolean,
    val createdAt: String,
    val updatedAt: String
)