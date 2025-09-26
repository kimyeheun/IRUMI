package com.example.irumi.data.datasource.events

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.events.PuzzlesResponse
import com.example.irumi.data.dto.response.events.EventResponse
import com.example.irumi.data.dto.response.events.EventsRoomResponse

interface EventsDataSource {
    suspend fun getEventsRoom(
    ): BaseResponse<EventsRoomResponse>
    suspend fun enterEventsRoom(
        roomCode: String
    ): BaseResponse<EventsRoomResponse>

    suspend fun createEventsRoom(
        maxMembers: Int
    ): BaseResponse<EventsRoomResponse>

    suspend fun leaveEventsRoom(
    ): BaseResponse<EventResponse>

    suspend fun fillPuzzle(
    ): BaseResponse<PuzzlesResponse>
}