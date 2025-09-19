package com.ssafy.pocketc_backend.domain.main.controller;

import com.ssafy.pocketc_backend.domain.main.dto.MainResponse;
import com.ssafy.pocketc_backend.domain.main.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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




}