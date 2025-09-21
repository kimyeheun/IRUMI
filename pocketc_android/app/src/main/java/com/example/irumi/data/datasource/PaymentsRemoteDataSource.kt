package com.example.irumi.data.datasource

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.PaymentCheckRequest
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.data.dto.response.PaymentsResponse
import com.example.irumi.data.service.PaymentsService
import javax.inject.Inject

class PaymentsRemoteDataSource @Inject constructor(
    private val paymentsService: PaymentsService
) : PaymentsDataSource {
    override suspend fun getPaymentDetail(transactionId: Int): BaseResponse<PaymentDetailResponse> =
        paymentsService.getPaymentDetail(transactionId)

    override suspend fun getPayments(): BaseResponse<PaymentsResponse> =
        paymentsService.getPayments()

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<PaymentDetailResponse> =
        paymentsService.putPaymentDetail(transactionId, request)

    override suspend fun patchPaymentDetail(transactionId: Int): BaseResponse<PaymentCheckRequest> =
        paymentsService.patchPaymentDetail(transactionId)

}