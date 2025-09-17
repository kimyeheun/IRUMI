package com.ssafy.pocketc_backend.domain.event.controller;

import com.ssafy.pocketc_backend.domain.event.dto.response.RoomResDto;
import com.ssafy.pocketc_backend.domain.event.service.EventService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.event.exception.EventSuccessType.SUCCESS_GET_ROOM;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    @GetMapping("/users/room")
    public ResponseEntity<ApiResponse<RoomResDto>> getEvents(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_ROOM,
                eventService.getRoom(principal)
        ));
    }
}
