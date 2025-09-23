package com.example.irumi.data.mapper

import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentDetailResponse
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.domain.entity.payments.PaymentsHistoryEntity

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

fun PaymentEntity.toPaymentEditRequest() =
    PaymentEditRequest(
        date = date,
        amount = amount,
        majorCategory = majorCategory,
        subCategory = subCategory,
        merchant = merchantName,
        isFixed = isFixed
    )
