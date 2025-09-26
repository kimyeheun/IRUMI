package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.stats.StatsDataSource
import com.example.irumi.data.dto.response.stats.MonthStatsResponse
import com.example.irumi.domain.repository.StatsRepository
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDataSource: StatsDataSource
) : StatsRepository {
    // TODO Mapper 적용
    override suspend fun getMonthStatistics(month: String): Result<MonthStatsResponse> {
        return runCatching { statsDataSource.getMonthStatistics(month).data!! }
    }
}