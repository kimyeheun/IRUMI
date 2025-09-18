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

    public void updateTotalExpense(Integer userId, LocalDate month, Integer expense) {

        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));

        Integer total = report.getMonthlyTotalExpense();
        report.setMonthlyTotalExpense(total + expense);
    }

    public void updateFixedExpense(Integer userId, LocalDate month, Integer expense) {

        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));

        Integer total = report.getMonthlyFixedExpense();
        report.setMonthlyFixedExpense(total + expense);
    }
}
