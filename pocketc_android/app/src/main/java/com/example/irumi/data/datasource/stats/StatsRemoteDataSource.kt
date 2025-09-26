package com.example.irumi.data.datasource.stats

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.data.service.StatsService
import javax.inject.Inject

class StatsRemoteDataSource @Inject constructor(
    private val statsService: StatsService
) : StatsDataSource {
    override suspend fun getMonthStatistics(month: String): BaseResponse<MonthStatsResponse> =
        statsService.getMonthStatistics(month)

}