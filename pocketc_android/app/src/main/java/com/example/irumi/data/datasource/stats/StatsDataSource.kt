package com.example.irumi.data.datasource.stats

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.stats.MonthStatsResponse

interface StatsDataSource {
    suspend fun getMonthStatistics(
        month: String
    ): BaseResponse<MonthStatsResponse>
}