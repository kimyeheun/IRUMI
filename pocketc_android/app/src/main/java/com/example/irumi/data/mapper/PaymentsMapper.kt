package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.Payment
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.data.dto.response.PaymentsResponse
import com.example.irumi.domain.entity.PaymentEntity
import com.example.irumi.domain.entity.PaymentsHistoryEntity

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

fun Payment.toPaymentEntity() =
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
fun PaymentsResponse.toPaymentEntity(): List<PaymentEntity> {
    return this.payments.map { it.toPaymentEntity() }
}

fun PaymentsResponse.toPaymentsHistoryEntity(): PaymentsHistoryEntity {
    return PaymentsHistoryEntity(
        payments = this.payments.map { it.toPaymentEntity() },
        totalSpending = this.totalSpending
    )
}