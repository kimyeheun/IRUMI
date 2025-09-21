package com.example.irumi.data.datasource

import com.example.irumi.data.dto.response.EventsRoomResponse

interface EventsDataSource {
    suspend fun getEventsRoomResponse(): EventsRoomResponse
}