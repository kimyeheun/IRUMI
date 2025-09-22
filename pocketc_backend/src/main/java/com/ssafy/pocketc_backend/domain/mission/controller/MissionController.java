package com.ssafy.pocketc_backend.domain.mission.controller;

import com.ssafy.pocketc_backend.domain.mission.dto.response.MissionResDto;
import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.mission.exception.MissionSuccessType.SUCCESS_GET_MISSIONS;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

    @GetMapping("/users/missions")
    public ResponseEntity<ApiResponse<MissionResDto>> getMissions(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MISSIONS,
                missionService.getMissions(userId(principal))
        ));
    }
}
