package com.ssafy.pocketc_backend.domain.main.controller;

import com.ssafy.pocketc_backend.domain.event.dto.response.BadgeResDto;
import com.ssafy.pocketc_backend.domain.event.service.EventService;
import com.ssafy.pocketc_backend.domain.main.dto.*;
import com.ssafy.pocketc_backend.domain.main.service.MainService;
import com.ssafy.pocketc_backend.domain.user.service.UserService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.event.exception.EventSuccessType.SUCCESS_GET_BADGES;
import static com.ssafy.pocketc_backend.domain.main.exception.MainSuccessType.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final UserService userService;
    private final EventService  eventService;

    private int userId(Principal principal) {return Integer.parseInt(principal.getName());}

    @Operation(summary = "친구와 내 절약점수+총 지출 조회")
    @GetMapping("/daily/{friendId}")
    public ResponseEntity<ApiResponse<DailyCompareResponse>> getDailyStats(
            Principal principal, @PathVariable Integer friendId) {
        MainResponse myStats = mainService.getDailyScoreAndTotal(userId(principal));
        MainResponse friendStats = mainService.getDailyScoreAndTotal(friendId);
        DailyCompareResponse response = new DailyCompareResponse(myStats, friendStats);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_GET_DAILY_STAT, response));
    }

    @Operation(summary = "내 절약점수+총 지출 조회")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<MainResponse>> getDailyStats(
            Principal principal) {
        MainResponse response = mainService.getDailyScoreAndTotal(userId(principal));
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_GET_DAILY_STAT, response));
    }

    @Operation(summary = "스트릭 조회")
    @GetMapping("/streaks")
    public ResponseEntity<ApiResponse<StreakResDto>> getStreaks(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_STREAKS,
                mainService.getStreaks(userId(principal))
        ));
    }

    @Operation(summary = "친구 이름과 스트릭 조회")
    @GetMapping("/streaks/{friendId}")
    public ResponseEntity<ApiResponse<StreakCompareResponse>> getFriendStreaks(@PathVariable Integer friendId) {
        String friendName = userService.getProfile(friendId).name();
        StreakResDto friendStreak = mainService.getStreaks(friendId);
        StreakCompareResponse response = new StreakCompareResponse(friendName, friendStreak);
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_STREAKS, response
        ));
    }

    @GetMapping("/badges/{friendId}")
    public ResponseEntity<ApiResponse<BadgeResDto>> getFriendBadges(@PathVariable Integer friendId) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_BADGES,
                eventService.getBadges(friendId)
        ));
    }

    @GetMapping("/follower/{followerId}")
    public ResponseEntity<ApiResponse<FollowerInfoDto>> getFollowerInfo(@PathVariable("followerId") Integer followerId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_FOLLOWER_INFO,
                mainService.getFollowerInfo(followerId, userId(principal))
        ));
    }
}