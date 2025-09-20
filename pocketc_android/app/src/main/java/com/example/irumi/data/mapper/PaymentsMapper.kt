package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.domain.entity.PaymentEntity

fun PaymentDetailResponse.toPaymentEntity() =
    PaymentEntity(
        paymentId = transactionId,
        date = date,
        amount = amount,
        majorCategory = majorCategory,
        subCategory = subCategory,
        merchantName = merchantName,
        isApplied = isApplied,
        isFixed = isFixed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )