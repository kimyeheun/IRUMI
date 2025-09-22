package com.example.irumi.domain.entity

import com.example.irumi.data.dto.response.RoomStatus

data class RoomEntity(
    val roomId: Int,
    val createdAt: String,
    val maxMembers: Int,
    val puzzleAttempts: Int,
    val status: RoomStatus,
    val roomCode: String,
    val puzzles: List<PuzzleEntity>,
    val ranks: List<RankEntity>,
    val members: List<MemberEntity>
)

data class PuzzleEntity(
    val pieceId: Int,
    val row: Int,
    val column: Int,
    val userId: Int
)

data class RankEntity(
    val userId: Int,
    val rank: Int,
    val count: Int
)