package com.example.irumi.domain.repository

import com.example.irumi.data.dto.response.stats.MonthStatsResponse

interface StatsRepository {
    suspend fun getMonthStatistics(
        month: String
    ): Result<MonthStatsResponse>
}