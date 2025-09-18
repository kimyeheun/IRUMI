package com.ssafy.pocketc_backend.domain.event.dto.response;

import com.ssafy.pocketc_backend.domain.event.entity.Event;

import java.time.LocalDateTime;

public record EventDto (
        Integer eventId,
        String eventName,
        String eventDescription,
        String eventImageUrl,
        String badgeName,
        String badgeDescription,
        String badgeImageUrl,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
    public static EventDto from(Event event) {
        return new EventDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventImageUrl(),
                event.getBadgeName(),
                event.getBadgeDescription(),
                event.getBadgeImageUrl(),
                event.getStartAt(),
                event.getEndAt()
        );
    }
}
