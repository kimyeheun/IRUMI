package com.example.irumi.data.datasource.events

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.Event
import com.example.irumi.data.dto.response.EventsRoomResponse
import com.example.irumi.data.dto.response.PuzzlesResponse

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
    ): BaseResponse<Event>

    suspend fun fillPuzzle(
    ): BaseResponse<PuzzlesResponse>
}