package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.EventsDataSource
import com.example.irumi.data.mapper.toEventEntity
import com.example.irumi.data.mapper.toMemberEntity
import com.example.irumi.data.mapper.toPuzzleEntity
import com.example.irumi.data.mapper.toRankEntity
import com.example.irumi.data.mapper.toRoomEntity
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(private val eventsDataSource: EventsDataSource) : EventRepository {
    override suspend fun getEventsRoomData(): Pair<RoomEntity, EventEntity> {
        val response = eventsDataSource.getEventsRoomResponse()
        val roomEntity = response.room.toRoomEntity(
            response.room.puzzles.map { it.toPuzzleEntity() },
            response.room.ranks.map { it.toRankEntity() },
            response.room.members.map { it.toMemberEntity() }
        )
        val eventEntity = response.event.toEventEntity()
        return Pair(roomEntity, eventEntity)
    }
}