package com.ssafy.pocketc_backend.domain.main.controller;

import com.ssafy.pocketc_backend.domain.main.dto.MainResponse;
import com.ssafy.pocketc_backend.domain.main.dto.StreakResDto;
import com.ssafy.pocketc_backend.domain.main.service.MainService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.main.exception.MainSuccessType.SUCCESS_GET_STREAKS;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    @Operation(summary = "절약점수와 오늘 총 지출 조회")

    @GetMapping("/{userId}/daily")
    public ResponseEntity<MainResponse> getDailyStats(
            @PathVariable Integer userId) {

        MainResponse dto = mainService.getDailyScoreAndTotal(userId);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/streaks")
    public ResponseEntity<ApiResponse<StreakResDto>> getStreaks(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_STREAKS,
                mainService.getStreaks(Integer.parseInt(principal.getName()))
        ));
    }
}