package com.example.irumi.domain.repository

import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.RoomEntity

interface EventRepository {
    suspend fun getEventsRoomData(): Pair<RoomEntity, EventEntity>
}