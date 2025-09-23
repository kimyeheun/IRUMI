package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.PaymentCheckRequest
import com.example.irumi.data.dto.response.payments.PaymentDetailResponse
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentsService {
    @GET("transactions/{transactionId}")
    suspend fun getPaymentDetail(
        @Path("transactionId") transactionId: Int,
    ): BaseResponse<PaymentDetailResponse>

    @GET("transactions")
    suspend fun getPayments(
        @Query("month") month: String,
    ): BaseResponse<PaymentsResponse>

    @PUT("transactions/{transactionId}")
    suspend fun putPaymentDetail(
        @Path("transactionId") transactionId: Int,
        @Body request: PaymentEditRequest
    ): BaseResponse<PaymentDetailResponse>

    @PATCH("transactions/{transactionId}")
    suspend fun patchPaymentDetail(
        @Path("transactionId") transactionId: Int,
    ): BaseResponse<PaymentCheckRequest>

}