package com.example.irumi.data.datasource.payments

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.PaymentCheckRequest
import com.example.irumi.data.dto.response.payments.PaymentDetailResponse
import com.example.irumi.data.dto.response.payments.PaymentsResponse

interface PaymentsDataSource {
    suspend fun getPaymentDetail(
        transactionId: Int,
    ): BaseResponse<PaymentDetailResponse>

    suspend fun getPayments(
    ): BaseResponse<PaymentsResponse>

    suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<PaymentDetailResponse>

   suspend fun patchPaymentDetail(
        transactionId: Int,
    ): BaseResponse<PaymentCheckRequest>
}