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
        val dummyPayments = listOf(
            com.example.irumi.data.dto.response.Payment(1, "2025-09-12T10:00:00Z", 25000, 1, 1, "스타벅스", true, false, "", ""),
            com.example.irumi.data.dto.response.Payment(2, "2025-09-12T11:00:00Z", 15000, 1, 2, "메가커피", false, false, "", ""),
            com.example.irumi.data.dto.response.Payment(3, "2025-09-11T15:30:00Z", 73456, 2, 1, "버거킹", true, true, "", "")
        )
        val dummyResponse = PaymentsResponse(
            payments = dummyPayments,
            totalSpending = dummyPayments.sumOf { it.amount }
        )
        return BaseResponse(status = 200, message = "success", data = dummyResponse)
    }

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<PaymentDetailResponse> {
        return BaseResponse(status = 200, message = "success", data = PaymentDetailResponse(transactionId, "", 0, 0, 0, "", false, false, "", ""))
    }

    override suspend fun patchPaymentDetail(transactionId: Int): BaseResponse<PaymentCheckRequest> {
        return BaseResponse(status = 200, message = "success", data = PaymentCheckRequest(transactionId))
    }
}