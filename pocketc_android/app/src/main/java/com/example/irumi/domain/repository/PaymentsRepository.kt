package com.example.irumi.domain.repository

import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.PaymentCheckRequest
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.data.dto.response.PaymentsResponse
import com.example.irumi.domain.entity.PaymentEntity

interface PaymentsRepository {
    suspend fun getPaymentDetail(
        transactionId: Int,
    ): Result<PaymentEntity>

    suspend fun getPayments(
    ): Result<PaymentsResponse>

    suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): Result<PaymentDetailResponse>

    suspend fun patchPaymentDetail(
        transactionId: Int,
    ): Result<PaymentCheckRequest>
}