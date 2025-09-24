package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.FillPuzzleEntity
import com.example.irumi.domain.entity.RoomEntity

interface EventsRepository {
    suspend fun getEventsRoom(): Result<Pair<RoomEntity?, EventEntity>>
    suspend fun enterEventsRoom(roomCode: String): Result<Pair<RoomEntity, EventEntity>>
    suspend fun createEventsRoom(maxMembers: Int): Result<Pair<RoomEntity, EventEntity>>
    suspend fun leaveEventsRoom(): Result<EventEntity>
    suspend fun fillPuzzle(): Result<FillPuzzleEntity>
}