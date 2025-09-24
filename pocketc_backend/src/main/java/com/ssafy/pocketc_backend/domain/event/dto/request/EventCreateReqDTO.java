package com.ssafy.pocketc_backend.domain.event.dto.request;

public record EventCreateReqDTO(
        String eventName,
        String eventDescription,
        String badgeImageUrl,
        String eventImageUrl,
        String badgeName,
        String badgeDescription
) {
    public static EventCreateReqDTO of(String eventName, String eventDescription, String badgeImageUrl, String eventImageUrl, String badgeName, String badgeDescription) {
        return new EventCreateReqDTO(eventName, eventDescription, badgeImageUrl, eventImageUrl, badgeName, badgeDescription);
    }
}
