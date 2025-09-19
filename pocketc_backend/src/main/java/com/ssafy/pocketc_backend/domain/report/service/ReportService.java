package com.ssafy.pocketc_backend.domain.report.service;

import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.ssafy.pocketc_backend.domain.report.exception.ReportErrorType.ERROR_GET_MONTHLY_TOTAL_EXPENSE;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * 월별 총지출액 수정
     */
    public void updateMonthlyTotalExpense(Integer userId, LocalDate month, Long expense) {
        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));
        Long total = report.getMonthlyTotalExpense();
        report.setMonthlyTotalExpense(total + expense);
    }

    /**
     * 월별 고정비 지출액 수정
     */
    public void updateMonthlyFixedExpense(Integer userId, LocalDate month, Long expense) {
        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));
        Long total = report.getMonthlyFixedExpense();
        report.setMonthlyFixedExpense(total + expense);
    }

}
