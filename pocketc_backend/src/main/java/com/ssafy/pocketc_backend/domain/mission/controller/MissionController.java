package com.ssafy.pocketc_backend.domain.mission.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.pocketc_backend.domain.mission.dto.request.MissionSelectedDto;
import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionResDto;
import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.mission.exception.MissionSuccessType.SUCCESS_CHOOSE_MISSIONS;
import static com.ssafy.pocketc_backend.domain.mission.exception.MissionSuccessType.SUCCESS_GET_MISSIONS;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    private Integer userId(Principal principal) {
        return Integer.parseInt(principal.getName());
    }

    @GetMapping("/users/missions")
    public ResponseEntity<ApiResponse<MissionResDto>> getMissions(Principal principal) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MISSIONS,
                missionService.getMissions(userId(principal))
        ));
    }

    @PostMapping("/users/missions")
    public ResponseEntity<ApiResponse<MissionResDto>> chooseMissions(@RequestBody MissionSelectedDto dto, Principal principal) throws JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_CHOOSE_MISSIONS,
                missionService.chooseMissions(dto, userId(principal))
        ));
    }

    @DeleteMapping("/admin/redis")
    public ResponseEntity<?> deleteRedis() {
        missionService.deleteRedis();
        return ResponseEntity.ok("성공");
    }
}