package com.example.irumi.data.service

import com.example.irumi.core.network.BaseResponse
import com.example.irumi.data.dto.response.Event
import com.example.irumi.data.dto.response.EventsRoomResponse
import com.example.irumi.data.dto.response.PuzzlesResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EventsService {
    /**
     * 이벤트 방 정보 조회 API
     */
    @GET("users/room")
    suspend fun getEventsRoom(
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 입장 API
     */
    @POST("event/room/join?roomCode={roomCode}")
    suspend fun enterEventsRoom(
        @Path("roomCode") roomCode: String
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 생성 API
     */
    @POST("event/room")
    suspend fun createEventsRoom(
        @Query("maxMembers") maxMembers: Int
    ): BaseResponse<EventsRoomResponse>

    /**
     * 이벤트 방 나가기 API
     */
    @POST("event/room")
    suspend fun leaveEventsRoom(
    ): BaseResponse<Event>

    /**
     * 퍼즐 채우기 API
     */
    @POST("event/fill")
    suspend fun fillPuzzle(
    ): BaseResponse<PuzzlesResponse>

}