package com.example.irumi.domain.entity

data class FillPuzzleEntity(
    val puzzles: List<PuzzleEntity>,
    val ranks: List<RankEntity>,
    val puzzleAttempts: Int
)
