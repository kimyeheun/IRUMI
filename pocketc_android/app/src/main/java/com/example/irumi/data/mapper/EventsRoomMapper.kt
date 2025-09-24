package com.example.irumi.data.mapper

import com.example.irumi.data.dto.response.events.Event
import com.example.irumi.data.dto.response.events.Member
import com.example.irumi.data.dto.response.events.Puzzle
import com.example.irumi.data.dto.response.events.Rank
import com.example.irumi.data.dto.response.events.Room
import com.example.irumi.domain.entity.EventEntity
import com.example.irumi.domain.entity.MemberEntity
import com.example.irumi.domain.entity.PuzzleEntity
import com.example.irumi.domain.entity.RankEntity
import com.example.irumi.domain.entity.RoomEntity


fun Room.toRoomEntity(puzzles: List<PuzzleEntity>, ranks: List<RankEntity>, members: List<MemberEntity>): RoomEntity {
    return RoomEntity(
        roomId = this.roomId,
        createdAt = this.createdAt,
        maxMembers = this.maxMembers,
        puzzleAttempts = this.puzzleAttempts,
        status = this.status,
        roomCode = this.roomCode,
        puzzles = puzzles,
        ranks = ranks,
        members = members,
        totalPieces = when(this.maxMembers) {
            2 -> 25
            3 -> 49
            else -> 81
        }
    )
}

fun Event.toEventEntity(): EventEntity {
    return EventEntity(
        eventId = this.eventId,
        eventName = this.eventName,
        eventDescription = this.eventDescription,
        eventImageUrl = this.eventImageUrl,
        badgeName = this.badgeName,
        badgeDescription = this.badgeDescription,
        startAt = this.startAt,
        endAt = this.endAt
    )
}

fun Puzzle.toPuzzleEntity(): PuzzleEntity {
    return PuzzleEntity(
        pieceId = this.pieceId,
        row = this.row,
        column = this.column,
        userId = this.userId
    )
}

fun Rank.toRankEntity(): RankEntity {
    return RankEntity(
        userId = this.userId,
        rank = this.rank,
        count = this.count
    )
}

fun Member.toMemberEntity(): MemberEntity {
    return MemberEntity(
        userId = this.userId,
        name = this.name,
        profileImageUrl = this.profileImageUrl,
        isFriend = this.isFriend
    )
}