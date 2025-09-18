package com.ssafy.pocketc_backend.domain.report.service;

import com.ssafy.pocketc_backend.domain.report.dto.response.MonthlyBudgetAndTotalExpenseResDto;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

import static com.ssafy.pocketc_backend.domain.report.exception.ReportErrorType.ERROR_GET_MONTHLY_BUDGET_AND_TOTAL_EXPENSE;
import static com.ssafy.pocketc_backend.domain.report.exception.ReportErrorType.ERROR_GET_MONTHLY_TOTAL_EXPENSE;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * 월별 총지출액 수정
     */
    public void updateMonthlyTotalExpense(Integer userId, LocalDate month, Integer expense) {
        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));
        Integer total = report.getMonthlyTotalExpense();
        report.setMonthlyTotalExpense(total + expense);
    }

    /**
     * 월별 고정비 지출액 수정
     */
    public void updateMonthlyFixedExpense(Integer userId, LocalDate month, Integer expense) {
        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));
        Integer total = report.getMonthlyFixedExpense();
        report.setMonthlyFixedExpense(total + expense);
    }

    /**
     * 월간 예산, 지출액 조회
     */
    public MonthlyBudgetAndTotalExpenseResDto getMonthlyBudgetAndTotalExpense(Integer userId, LocalDate date) {
        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, date)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));

        return new MonthlyBudgetAndTotalExpenseResDto(report.getUser().getBudget(), report.getMonthlyTotalExpense());
    }
}
