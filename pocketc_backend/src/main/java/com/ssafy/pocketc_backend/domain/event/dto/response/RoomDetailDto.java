package com.ssafy.pocketc_backend.domain.event.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RoomDetailDto(
        Integer roomId,
        LocalDateTime createdAt,
        Integer maxMembers,
        Integer puzzleAttempts,
        String status,
        String roomCode,
        List<PuzzleDto> puzzles,
        List<RankDto> ranks,
        List<MemberDto> members
) {
    public static RoomDetailDto of(
            Integer roomId,
            LocalDateTime createdAt,
            Integer maxMembers,
            Integer puzzleAttempts,
            String status,
            String roomCode,
            List<PuzzleDto> puzzles,
            List<RankDto> ranks,
            List<MemberDto> members
    ) {
        return new RoomDetailDto(roomId, createdAt, maxMembers, puzzleAttempts, status, roomCode, puzzles, ranks, members);
    }
}