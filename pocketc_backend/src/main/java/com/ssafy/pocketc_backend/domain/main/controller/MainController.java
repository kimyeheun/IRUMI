package com.ssafy.pocketc_backend.domain.main.controller;

import com.ssafy.pocketc_backend.domain.main.dto.DailyCompareResponse;
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

import static com.ssafy.pocketc_backend.domain.main.exception.MainSuccessType.SUCCESS_GET_DAILY_STAT;
import static com.ssafy.pocketc_backend.domain.main.exception.MainSuccessType.SUCCESS_GET_STREAKS;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    @Operation(summary = "친구와 내 절약점수+총 지출 조회")
    @GetMapping("/daily/{friendId}")
    public ResponseEntity<ApiResponse<DailyCompareResponse>> getDailyStats(
            Principal principal,@PathVariable Integer friendId) {
        Integer myId = Integer.valueOf(principal.getName());
        MainResponse myStats = mainService.getDailyScoreAndTotal(myId);
        MainResponse friendStats = mainService.getDailyScoreAndTotal(friendId);
        DailyCompareResponse response = new DailyCompareResponse(myStats, friendStats);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_GET_DAILY_STAT,response));
    }

    @Operation(summary = "스트릭 조회")
    @GetMapping("/streaks")
    public ResponseEntity<ApiResponse<StreakResDto>> getStreaks(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_STREAKS,
                mainService.getStreaks(Integer.parseInt(principal.getName()))
        ));
    }
}