package com.example.irumi.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PaymentEditRequest(
    val date: String,
    val amount: Int,
    val majorCategory: Int,
    val subCategory: Int,
    val merchant: String,
    val isFixed: Boolean
)