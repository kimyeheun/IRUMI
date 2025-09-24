package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.events.PuzzlesResponse
import com.example.irumi.data.dto.response.events.EventResponse
import com.example.irumi.data.dto.response.events.EventsRoomResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EventsService {
    /**
     * 이벤트 방 정보 조회 API
     */
    @GET("/api/v1/event/room")
    suspend fun getEventsRoom(
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 입장 API
     */
    @POST("/api/v1/event/room/join")
    suspend fun enterEventsRoom(
        @Query("roomCode") roomCode: String
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 생성 API
     */
    @POST("/api/v1/event/room")
    suspend fun createEventsRoom(
        @Query("maxMembers") maxMembers: Int
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 나가기 API
     */
    @DELETE("/api/v1/event/room")
    suspend fun leaveEventsRoom(
    ): BaseResponse<EventResponse>

    /**
     * 퍼즐 채우기 API
     */
    @POST("/api/v1/event/fill")
    suspend fun fillPuzzle(
    ): BaseResponse<PuzzlesResponse>

}