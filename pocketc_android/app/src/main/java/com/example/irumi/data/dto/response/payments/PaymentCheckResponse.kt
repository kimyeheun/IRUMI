package com.example.irumi.data.dto.response.payments

import kotlinx.serialization.Serializable

@Serializable
data class PaymentCheckRequest(
    val transactionId: Int
)