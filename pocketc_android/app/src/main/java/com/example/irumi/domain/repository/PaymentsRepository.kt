package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.BaseEntity
import com.example.irumi.domain.entity.payments.PaymentEntity
import com.example.irumi.domain.entity.payments.PaymentsHistoryEntity

interface PaymentsRepository {
    suspend fun getPaymentDetail(
        transactionId: Int,
    ): Result<PaymentEntity>

    suspend fun getPayments(
        year: Int,
        month: Int
    ): Result<PaymentsHistoryEntity>

    suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEntity
    ): Result<PaymentEntity>

    suspend fun checkPaymentDetail(
        transactionId: Int,
    ): Result<BaseEntity<Void>>
}