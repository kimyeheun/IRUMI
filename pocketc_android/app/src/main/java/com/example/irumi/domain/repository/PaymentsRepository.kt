package com.example.irumi.domain.repository

import com.example.irumi.data.dto.response.payments.PaymentCheckRequest
import com.example.irumi.domain.entity.PaymentEntity
import com.example.irumi.domain.entity.PaymentsHistoryEntity

interface PaymentsRepository {
    suspend fun getPaymentDetail(
        transactionId: Int,
    ): Result<PaymentEntity>

    suspend fun getPayments(
        month: String
    ): Result<PaymentsHistoryEntity>

    suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEntity
    ): Result<PaymentEntity>

    suspend fun patchPaymentDetail(
        transactionId: Int,
    ): Result<PaymentCheckRequest>
}