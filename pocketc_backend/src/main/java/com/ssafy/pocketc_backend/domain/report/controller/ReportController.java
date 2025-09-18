package com.ssafy.pocketc_backend.domain.report.controller;

import com.ssafy.pocketc_backend.domain.report.dto.response.MonthlyBudgetAndTotalExpenseResDto;
import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.ssafy.pocketc_backend.domain.report.exception.ReportSuccessType.SUCCESS_GET_MONTHLY_TOTAL_EXPENSE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ReportController {

    private final ReportService reportService;

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }


    @GetMapping("/statistics/monthly")
    public ResponseEntity<ApiResponse<MonthlyBudgetAndTotalExpenseResDto>> getMonthlyTotalExpense(Principal principal, @RequestParam("month") LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_MONTHLY_TOTAL_EXPENSE,
                reportService.getMonthlyBudgetAndTotalExpense(userId(principal), date)
        ));
    }

}
