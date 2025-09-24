package com.example.irumi.data.repositoryimpl

import com.example.irumi.data.datasource.events.EventsDataSource
import com.example.irumi.data.mapper.toEventEntity
import com.example.irumi.data.mapper.toMemberEntity
import com.example.irumi.data.mapper.toPuzzleEntity
import com.example.irumi.data.mapper.toRankEntity
import com.example.irumi.data.mapper.toRoomEntity
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.FillPuzzleEntity
import com.example.irumi.domain.entity.RoomEntity
import com.example.irumi.domain.repository.EventsRepository
import javax.inject.Inject

class EventsRepositoryImpl @Inject constructor(
    private val eventDataSource: EventsDataSource
) : EventsRepository {
    override suspend fun getEventsRoom(): Result<Pair<RoomEntity?, EventEntity>> {
        return runCatching {
            val response = eventDataSource.getEventsRoom().data!!
            val roomEntity = response.room?.toRoomEntity(
                response.room.puzzles.map { it.toPuzzleEntity() },
                response.room.ranks.map { it.toRankEntity() },
                response.room.members.map { it.toMemberEntity() }
            )
            val eventEntity = response.event.toEventEntity()
            Pair(roomEntity, eventEntity)
        }
    }

    override suspend fun enterEventsRoom(roomCode: String): Result<Pair<RoomEntity, EventEntity>> {
        return runCatching {
            val response = eventDataSource.enterEventsRoom(roomCode).data!!
            val roomEntity = response.room!!.toRoomEntity(
                response.room.puzzles.map { it.toPuzzleEntity() },
                response.room.ranks.map { it.toRankEntity() },
                response.room.members.map { it.toMemberEntity() }
            )
            val eventEntity = response.event.toEventEntity()
            Pair(roomEntity, eventEntity)
        }
    }

    override suspend fun createEventsRoom(maxMembers: Int): Result<Pair<RoomEntity, EventEntity>> {
        return runCatching {
            val response = eventDataSource.createEventsRoom(maxMembers).data!!
            val roomEntity = response.room!!.toRoomEntity(
                response.room.puzzles.map { it.toPuzzleEntity() },
                response.room.ranks.map { it.toRankEntity() },
                response.room.members.map { it.toMemberEntity() }
            )
            val eventEntity = response.event.toEventEntity()
            Pair(roomEntity, eventEntity)
        }
    }

    override suspend fun leaveEventsRoom(): Result<EventEntity> {
        return runCatching {
            eventDataSource.leaveEventsRoom().data!!.event.toEventEntity()
        }
    }

    override suspend fun fillPuzzle(): Result<FillPuzzleEntity> {
        return runCatching {
            val response = eventDataSource.fillPuzzle().data!!
            FillPuzzleEntity(
                response.puzzles.map { it.toPuzzleEntity() },
                response.ranks.map { it.toRankEntity() },
                response.puzzleAttempts)
        }
    }
}