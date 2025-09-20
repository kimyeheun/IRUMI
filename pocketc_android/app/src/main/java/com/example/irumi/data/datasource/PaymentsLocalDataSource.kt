package com.example.irumi.data.datasource

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.PaymentCheckRequest
import com.example.irumi.data.dto.response.PaymentDetailResponse
import com.example.irumi.data.dto.response.PaymentsResponse
import javax.inject.Inject


class PaymentsLocalDataSource @Inject constructor() : PaymentsDataSource {
    override suspend fun getPaymentDetail(transactionId: Int): BaseResponse<PaymentDetailResponse> {
        return BaseResponse(status = 200,
            message = "success",
            data = PaymentDetailResponse(
                transactionId = 501,
                date = "2025-09-11T14:25:00Z",
                amount = 13500,
                majorCategory = 2,
                subCategory = 3,
                merchantName = "스타벅스",
                isApplied = true,
                isFixed = false,
                createdAt = "2025-09-11T14:25:30Z",
                updatedAt = "2025-09-11T14:25:30Z"
            ))
    }

    override suspend fun getPayments(): BaseResponse<PaymentsResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<PaymentDetailResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchPaymentDetail(transactionId: Int): BaseResponse<PaymentCheckRequest> {
        TODO("Not yet implemented")
    }
}