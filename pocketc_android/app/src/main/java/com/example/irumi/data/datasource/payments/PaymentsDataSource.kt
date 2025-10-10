package com.example.irumi.data.datasource.payments

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.payments.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentsResponse

interface PaymentsDataSource {
    suspend fun getPaymentDetail(
        transactionId: Int,
    ): BaseResponse<Payment>

    suspend fun getPayments(
        year: Int,
        month: Int
    ): BaseResponse<PaymentsResponse>

    suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<Payment>

   suspend fun checkPaymentDetail(
        transactionId: Int,
    ): BaseResponse<Void>
}