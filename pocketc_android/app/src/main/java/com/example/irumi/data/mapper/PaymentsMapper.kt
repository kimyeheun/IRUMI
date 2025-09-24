package com.example.irumi.data.mapper

import com.example.irumi.data.dto.request.payments.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.domain.entity.payments.PaymentsHistoryEntity


fun Payment.toPaymentEntity() =
    PaymentEntity(
        paymentId = transactionId,
        date = transactedAt,
        amount = amount,
        majorCategory = majorId,
        subCategory = subId,
        merchantName = merchantName,
        isApplied = isApplied,
        isFixed = isFixed,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun PaymentsResponse.toPaymentsHistoryEntity(): PaymentsHistoryEntity {
    return PaymentsHistoryEntity(
        payments = this.transactions.map { it.toPaymentEntity() },
        totalSpending = this.totalSpending
    )
}

fun PaymentEntity.toPaymentEditRequest() =
    PaymentEditRequest(
        amount = amount,
        majorId = majorCategory,
        subId = subCategory,
        merchantName = merchantName,
        isFixed = isFixed
    )
