package com.ssafy.pocketc_backend.domain.event.dto.response;

import java.time.LocalDateTime;

public record RoomDetailDto(
        Integer roomId,
        Integer maxMembers,
        LocalDateTime createdAt,
        String status,
        String roomCode,
        EventResDto event
) {
    public static RoomDetailDto of(Integer roomId, Integer maxMembers, LocalDateTime createdAt, String status, String roomCode, EventResDto event) {
        return new RoomDetailDto(roomId, maxMembers, createdAt, status, roomCode, event);
    }
}