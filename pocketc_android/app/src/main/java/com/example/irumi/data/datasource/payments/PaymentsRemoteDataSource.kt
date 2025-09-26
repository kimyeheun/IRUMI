package com.example.irumi.data.datasource.payments

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.payments.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import com.example.irumi.data.service.PaymentsService
import javax.inject.Inject

class PaymentsRemoteDataSource @Inject constructor(
    private val paymentsService: PaymentsService
) : PaymentsDataSource {
    override suspend fun getPaymentDetail(transactionId: Int): BaseResponse<Payment> =
        paymentsService.getPaymentDetail(transactionId)

    override suspend fun getPayments(year: Int, month: Int): BaseResponse<PaymentsResponse> =
        paymentsService.getPayments(year, month)

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<Payment> =
        paymentsService.putPaymentDetail(transactionId, request)

    override suspend fun checkPaymentDetail(transactionId: Int): BaseResponse<Void> =
        paymentsService.checkPaymentDetail(transactionId)
}