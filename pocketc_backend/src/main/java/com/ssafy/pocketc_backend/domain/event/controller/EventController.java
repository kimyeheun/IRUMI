package com.ssafy.pocketc_backend.domain.event.controller;

import com.ssafy.pocketc_backend.domain.event.dto.request.EventCreateReqDTO;
import com.ssafy.pocketc_backend.domain.event.dto.response.BadgeResDto;
import com.ssafy.pocketc_backend.domain.event.dto.response.EventResDto;
import com.ssafy.pocketc_backend.domain.event.dto.response.PuzzleResDto;
import com.ssafy.pocketc_backend.domain.event.dto.response.RoomResDto;
import com.ssafy.pocketc_backend.domain.event.service.EventService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.event.exception.EventSuccessType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

    @GetMapping("/event/room")
    public ResponseEntity<ApiResponse<RoomResDto>> getRoom(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_ROOM,
                eventService.getRoom(userId(principal))
        ));
    }

    @PostMapping("/event/room/join")
    public ResponseEntity<ApiResponse<RoomResDto>> joinRoom(@RequestParam("roomCode") String roomCode, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_JOIN_ROOM,
                eventService.joinRoom(roomCode, userId(principal))
        ));
    }

    @PostMapping("/event/room")
    public ResponseEntity<ApiResponse<RoomResDto>> createRoom(@RequestParam("maxMembers") Integer maxMembers, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CREATE_ROOM,
                eventService.createRoom(maxMembers, userId(principal))
        ));
    }

    @DeleteMapping("/event/room")
    public ResponseEntity<ApiResponse<EventResDto>> leaveRoom(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_LEAVE_ROOM,
                eventService.leaveRoom(userId(principal))
        ));
    }

    @PostMapping("/event/fill")
    public ResponseEntity<ApiResponse<PuzzleResDto>> fillPuzzle(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_FILL_PUZZLES,
                eventService.fillPuzzle(userId(principal))
        ));
    }

    @GetMapping("/users/badges")
    public ResponseEntity<ApiResponse<BadgeResDto>> getBadges(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_BADGES,
                eventService.getBadges(userId(principal))
        ));
    }

    @PostMapping("/admin/events")
    public ResponseEntity<ApiResponse<?>> createEvent(@RequestParam EventCreateReqDTO dto) {
        eventService.createEvent(dto);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_CREATE_EVENT));
    }
}