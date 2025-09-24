package com.example.irumi.data.datasource.events

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.events.PuzzlesResponse
import com.example.irumi.data.dto.response.events.EventResponse
import com.example.irumi.data.dto.response.events.EventsRoomResponse
import com.example.irumi.data.service.EventsService
import javax.inject.Inject

class EventsRemoteDataSource @Inject constructor(
    private val eventsService: EventsService
): EventsDataSource {
    override suspend fun getEventsRoom(): BaseResponse<EventsRoomResponse> =
        eventsService.getEventsRoom()

    override suspend fun enterEventsRoom(roomCode: String): BaseResponse<EventsRoomResponse> =
        eventsService.enterEventsRoom(roomCode)

    override suspend fun createEventsRoom(maxMembers: Int): BaseResponse<EventsRoomResponse> =
        eventsService.createEventsRoom(maxMembers)

    override suspend fun leaveEventsRoom(): BaseResponse<EventResponse> =
        eventsService.leaveEventsRoom()

    override suspend fun fillPuzzle(): BaseResponse<PuzzlesResponse> =
        eventsService.fillPuzzle()

}