package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsService {
//    @GET("{userId}/mypage")
//    suspend fun getMyInfo(
//        @Path("userId") userId: Int,
//    ): BaseResponse<Payment>

    @GET("statistics/month/{month}")
    suspend fun getMonthStatistics(
        @Path("month") month: String,
    ): BaseResponse<MonthStatsResponse>

}