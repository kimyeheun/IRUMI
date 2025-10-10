package com.example.irumi.data.dto.request.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentEditRequest(
    val amount: Int,
    val majorId: Int,
    val subId: Int,
    val merchantName: String,
    val isFixed: Boolean
)