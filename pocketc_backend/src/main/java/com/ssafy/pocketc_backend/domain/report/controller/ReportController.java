package com.ssafy.pocketc_backend.domain.report.controller;

import com.ssafy.pocketc_backend.domain.report.dto.response.MonthlyReportResDto;
import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

import static com.ssafy.pocketc_backend.domain.report.exception.ReportSuccessType.SUCCESS_GET_MONTHLY_REPORT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ReportController {

    private final ReportService reportService;

    private Integer userId(Principal principal) {
        return Integer.parseInt(principal.getName());
    }

    @GetMapping("/statistics/month/{month}")
    public ResponseEntity<ApiResponse<MonthlyReportResDto>> getMonthlyStatistics(Principal principal, @PathVariable("month") LocalDate now) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MONTHLY_REPORT,
                reportService.getReport(userId(principal), now)
        ));
    }
}
