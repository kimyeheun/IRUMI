package com.example.irumi.data.dto.response

import java.time.LocalDateTime

data class PuzzlesResponse(
    val data: PuzzleData
)

data class PuzzleData(
    val puzzles: List<Puzzle>,
    val totalPieces: Int,
    val filledCount: Int
)

data class Puzzle(
    val pieceId: Int,
    val row: Int,
    val column: Int,
    val filledBy: User?, //todo 나중에 지우기
    val filledAt: LocalDateTime? //todo 나중에 지우기
)

data class User(
    val userId: Int,
    val nickname: String
)