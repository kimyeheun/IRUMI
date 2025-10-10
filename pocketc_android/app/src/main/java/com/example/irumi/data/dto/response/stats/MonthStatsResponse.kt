package com.example.irumi.data.dto.response.stats

import kotlinx.serialization.Serializable

@Serializable
data class MonthStatsResponse(
    val budget: Int,
    val currMonthExpense: Int,
    val lastMonthExpense: Int,
    val expenseByCategories: List<CategoryExpense>,
    val monthlySavingScoreList: List<MonthlySavingScore>
)

@Serializable
data class CategoryExpense(
    val categoryId: Int,
    val expense: Int
)

@Serializable
data class MonthlySavingScore(
    val month: String,      // "2025-04-01" 형식
    val savingScore: Double // 30.0
)
