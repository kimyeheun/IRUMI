package com.example.irumi.data.dto.response.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventsRoomResponse(
    @SerialName("room")
    val room: Room?,
    @SerialName("event")
    val event: Event
)

@Serializable
data class Room(
    @SerialName("roomId")
    val roomId: Int,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("maxMembers")
    val maxMembers: Int,
    @SerialName("puzzleAttempts")
    val puzzleAttempts: Int,
    @SerialName("status")
    val status: RoomStatus,
    @SerialName("roomCode")
    val roomCode: String,
    @SerialName("puzzles")
    val puzzles: List<Puzzle>,
    @SerialName("ranks")
    val ranks: List<Rank>,
    @SerialName("members")
    val members: List<Member>
)

// TODO 파일 분리
@Serializable
enum class RoomStatus {
    SUCCESS,
    IN_PROGRESS,
    FAILURE
}

@Serializable
data class Event(
    @SerialName("eventId")
    val eventId: Int,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("eventDescription")
    val eventDescription: String,
    @SerialName("eventImageUrl")
    val eventImageUrl: String,
    @SerialName("badgeImageUrl")
    val badgeImageUrl: String,
    @SerialName("badgeName")
    val badgeName: String,
    @SerialName("badgeDescription")
    val badgeDescription: String,
    @SerialName("startAt")
    val startAt: String,
    @SerialName("endAt")
    val endAt: String
)

@Serializable
data class Puzzle(
    @SerialName("pieceId")
    val pieceId: Int,
    @SerialName("row")
    val row: Int,
    @SerialName("column")
    val column: Int,
    @SerialName("userId")
    val userId: Int
)

@Serializable
data class Rank(
    @SerialName("userId")
    val userId: Int,
    @SerialName("rank")
    val rank: Int,
    @SerialName("count")
    val count: Int
)

@Serializable
data class Member(
    @SerialName("userId")
    val userId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String,
    @SerialName("isFriend")
    val isFriend: Boolean
)