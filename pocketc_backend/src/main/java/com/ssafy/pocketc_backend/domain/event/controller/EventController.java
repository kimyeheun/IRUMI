package com.ssafy.pocketc_backend.domain.event.controller;

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
@RequestMapping("/api/v1/event")
public class EventController {

    private final EventService eventService;

    @GetMapping("/room")
    public ResponseEntity<ApiResponse<RoomResDto>> getRoom(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_ROOM,
                eventService.getRoom(principal)
        ));
    }

    @PostMapping("/room/join")
    public ResponseEntity<ApiResponse<RoomResDto>> joinRoom(@RequestParam("roomCode") String roomCode, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_JOIN_ROOM,
                eventService.joinRoom(roomCode, principal)
        ));
    }
//
//    @PostMapping("/users/event/room")
//    public ResponseEntity<ApiResponse<RoomResDto>> createRoom(@RequestParam("maxMembers") Integer maxMembers, Principal principal) {
//        return ResponseEntity.ok(ApiResponse.success(
//                SUCCESS_CREATE_ROOM,
//                eventService.createRoom(maxMembers, principal)
//        ));
//    }
//
//    @DeleteMapping("/users/event/room")
//    public ResponseEntity<ApiResponse<?>> deleteRoom(Principal principal) {
//        eventService.leaveRoom(principal);
//        return ResponseEntity.ok(ApiResponse.success(
//                SUCCESS_LEAVE_ROOM
//        ));
//    }
//
//    @GetMapping("/users/event/room/members")
//    public ResponseEntity<ApiResponse<?>> getMembers(Principal principal) {
//        return ResponseEntity.ok(ApiResponse.success(
//                SUCCESS_GET_MEMBERS,
//                eventService.getMembers(principal)
//        ));
//    }
}