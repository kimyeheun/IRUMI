package com.example.irumi.data.datasource.payments

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.payments.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import javax.inject.Inject


class PaymentsLocalDataSource @Inject constructor() : PaymentsDataSource {
    override suspend fun getPaymentDetail(transactionId: Int): BaseResponse<Payment> {
        return BaseResponse(status = 200,
            message = "success",
            data = Payment(
                transactionId = 501,
                transactedAt = "2025-09-11T14:25:00Z",
                amount = 13500,
                majorId = 2,
                subId = 3,
                merchantName = "스타벅스",
                isApplied = true,
                isFixed = false,
                createdAt = "2025-09-11T14:25:30Z",
                updatedAt = "2025-09-11T14:25:30Z"
            ))
    }

    override suspend fun getPayments(year: Int, month: Int): BaseResponse<PaymentsResponse> {
        val dummyPayments = listOf(
            Payment(1, "2025-09-12T10:00:00Z", 25000, 1, 1, "스타벅스", true, false, "", ""),
            Payment(2, "2025-09-12T11:00:00Z", 15000, 1, 2, "메가커피", false, false, "", ""),
            Payment(3, "2025-09-11T15:30:00Z", 73456, 2, 1, "버거킹", true, true, "", "")
        )
        val dummyResponse = PaymentsResponse(
            transactions = dummyPayments,
            totalSpending = dummyPayments.sumOf { it.amount }
        )
        return BaseResponse(status = 200, message = "success", data = dummyResponse)
    }

    override suspend fun putPaymentDetail(
        transactionId: Int,
        request: PaymentEditRequest
    ): BaseResponse<Payment> {
        return BaseResponse(status = 200, message = "success", data = Payment(transactionId, "", 0, 0, 0, "", false, false, "", ""))
    }

    override suspend fun checkPaymentDetail(transactionId: Int): BaseResponse<Void> {
        return BaseResponse(status = 200, message = "success", data = null)
    }
}