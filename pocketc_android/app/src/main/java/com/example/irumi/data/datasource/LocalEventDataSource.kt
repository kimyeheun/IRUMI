package com.example.irumi.data.datasource

import com.example.irumi.data.dto.response.Event
import com.example.irumi.data.dto.response.EventsRoomResponse
import com.example.irumi.data.dto.response.Member
import com.example.irumi.data.dto.response.Puzzle
import com.example.irumi.data.dto.response.Rank
import com.example.irumi.data.dto.response.Room
import javax.inject.Inject

class LocalEventDataSource @Inject constructor() : EventsDataSource {
    override suspend fun getEventsRoomResponse(): EventsRoomResponse {
        return EventsRoomResponse(
            room = Room(
                roomId = 1,
                createdAt = "2025-09-21T10:00:00Z",
                maxMembers = 4,
                puzzleAttempts = 5,
                status = "IN_PROGRESS",
                roomCode = "ABCD123",
                puzzles = listOf(
                    Puzzle(1, 1, 1, 1),
                    Puzzle(2, 1, 2, 1),
                    Puzzle(3, 1, 4, 1),
                    Puzzle(4, 3, 3, 2),
                    Puzzle(5, 4, 4, 2),
                    Puzzle(6, 5, 5, 2),
                ),
                ranks = listOf(
                    Rank(1, 1, 3),
                    Rank(2, 2, 3)
                ),
                members = listOf(
                    Member(1, "절약왕", "https://cdn.example.com/users/123.png", true),
                    Member(2, "소비조절러", "https://cdn.example.com/users/124.png", false)
                )
            ),
            event = Event(
                eventId = 101,
                eventName = "가을맞이 절약 챌린지",
                eventDescription = "가을을 맞아 절약 챌린지에 참여하세요!",
                eventImageUrl = "https://mblogthumb-phinf.pstatic.net/MjAyMzEwMDhfMjMz/MDAxNjk2NzMyNTA3NzM1.O5iVGUwOEGFbxoqzH9H5H2qwFmbLNdOR7PmuuNE2PMAg.eY7eLpHanrC_AWz-9T2VCZamarnMq_5i6MBHboR2Z1Ug.JPEG.qmfosej/IMG_7989.JPG?type=w800",
                badgeName = "절약왕 뱃지",
                badgeDescription = "절약 챌린지 성공 시 획득",
                startAt = "2025-09-01T00:00:00Z",
                endAt = "2025-09-30T23:59:59Z"
            )
        )
    }
}