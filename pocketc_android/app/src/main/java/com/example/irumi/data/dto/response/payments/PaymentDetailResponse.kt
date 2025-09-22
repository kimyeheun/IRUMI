package com.example.irumi.data.dto.response.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDetailResponse(
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