package com.example.irumi.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuzzlesResponse(
    @SerialName("puzzles")
    val puzzles: List<Puzzle>,
    @SerialName("ranks")
    val ranks: List<Rank>,
    @SerialName("puzzleAttempts")
    val puzzleAttempts: Int
)
