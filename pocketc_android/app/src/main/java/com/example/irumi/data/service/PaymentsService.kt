package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.request.payments.PaymentEditRequest
import com.example.irumi.data.dto.response.payments.Payment
import com.example.irumi.data.dto.response.payments.PaymentsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentsService {
    @GET("transactions/{transactionId}")
    suspend fun getPaymentDetail(
        @Path("transactionId") transactionId: Int,
    ): BaseResponse<Payment>

    @GET("transactions")
    suspend fun getPayments(
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): BaseResponse<PaymentsResponse>

    @PUT("transactions/{transactionId}")
    suspend fun putPaymentDetail(
        @Path("transactionId") transactionId: Int,
        @Body request: PaymentEditRequest
    ): BaseResponse<Payment>

    @POST("transactions/{transactionId}")
    suspend fun checkPaymentDetail(
        @Path("transactionId") transactionId: Int,
    ): BaseResponse<Void>

}