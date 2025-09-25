package com.ssafy.pocketc_backend.domain.report.service;

import com.ssafy.pocketc_backend.domain.report.dto.response.ExpenseByCategoryDto;
import com.ssafy.pocketc_backend.domain.report.dto.response.MonthlyReportResDto;
import com.ssafy.pocketc_backend.domain.report.dto.response.MonthlySavingScoreDto;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ssafy.pocketc_backend.domain.report.exception.ReportErrorType.ERROR_GET_MONTHLY_TOTAL_EXPENSE;
import static com.ssafy.pocketc_backend.domain.transaction.exception.TransactionErrorType.ERROR_GET_MONTHLY_TRANSACTIONS;

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

        System.out.println(String.format("userId: %d, month: %s, expense: %d", userId, month, expense));

        Report report = reportRepository.findByUser_UserIdAndReportMonth(userId, month)
                .orElseThrow(() -> new CustomException(ERROR_GET_MONTHLY_TOTAL_EXPENSE));
        Long total = report.getMonthlyFixedExpense();
        report.setMonthlyFixedExpense(total + expense);
    }

    public MonthlyReportResDto getReport(Integer userId, LocalDate now) {
        // 입력된 시간이 현재 시간보다 미래 시간이라면 ERROR를 반환
        YearMonth yearMonth = YearMonth.from(now);
        if (yearMonth.isAfter(YearMonth.now())) throw new CustomException(ERROR_GET_MONTHLY_TRANSACTIONS);

        // 기본값이 들어있는 Report 생성
        List<Report> reports = new ArrayList();
        for (int i = 6; i >=0; i--) {
            Report report = new Report();
            report.setReportMonth(now.minusMonths(i).withDayOfMonth(1));
            report.setMonthlyBudget(0L);
            report.setMonthlyTotalExpense(0L);
            report.setMonthlyFixedExpense(0L);
            reports.add(report);
        }
        // 월별 Report 조회
        List<Report> findReports = reportRepository.findAllByUser_UserIdAndReportMonthBetweenOrderByReportMonthAsc(
                userId,
                now.minusMonths(6).withDayOfMonth(1),
                now.withDayOfMonth(now.lengthOfMonth())
        );
        Map<YearMonth, Report> foundedMap = findReports.stream()
                .collect(Collectors.toMap(
                        r -> YearMonth.from(r.getReportMonth()),
                        Function.identity()
                ));
        // 조회한 Report 매핑
        for (int i = 0; i < reports.size(); i++) {
            YearMonth ym = YearMonth.from(reports.get(i).getReportMonth());
            if (foundedMap.containsKey(ym)) {
                reports.set(i, foundedMap.get(ym));
            }
        }

        Collections.sort(reports);  // Comparable 기준인 reportMonth 오름차순

        // 이번 달 예산
        Long budget = reports.getLast().getMonthlyBudget();

        // 이번 달의 지출, 지난 달의 지출
        Long currMonthExpense = reports.getLast().getMonthlyTotalExpense();
        Long lastMonthExpense = reports.get(reports.size() - 2).getMonthlyTotalExpense()    ;

        // 카테고리별 결제내역 리스트
        List<ExpenseByCategoryDto> expenseByCategories = reportRepository.findExpenseByCategoryForMonth(
                userId,
                now.withDayOfMonth(1).atStartOfDay(),
                now.plusMonths(1).withDayOfMonth(1).atStartOfDay()
        );

        // 월별 절약 점수 반환
        List<MonthlySavingScoreDto> monthlySavingScoreList = getMonthlySavingScore(reports);

        return new MonthlyReportResDto(
                budget,
                currMonthExpense,
                lastMonthExpense,
                expenseByCategories,
                monthlySavingScoreList
        );
    }

    /**
     * 월별 절약 점수 List 계산
     *
     * @param reports : 지난 7달의 report 1번째 달의 데이터는 2번째 달의 절약 점수를 계산하는 데 사용합니다.
     */
    private List<MonthlySavingScoreDto> getMonthlySavingScore(List<Report> reports) {
        List<MonthlySavingScoreDto> monthlySavingScore = new ArrayList<>();

        // 가중치 설정
        double w1 = 0.7; // 예산 준수 비중
        double w2 = 0.3; // 고정비 효율 비중

        for (int i = 1; i < reports.size(); i++) {
            Report curr = reports.get(i);
            Report prev = reports.get(i - 1);

            long budget = curr.getMonthlyBudget();
            long currTotal = curr.getMonthlyTotalExpense();
            long currFixed = curr.getMonthlyFixedExpense();

            long lastTotal = prev.getMonthlyTotalExpense();
            long lastFixed = prev.getMonthlyFixedExpense();

            // B 점수 계산
            double bScore = 0.0;
            if (budget > 0) {
                double overRatio = (double) (currTotal - budget) / (double) budget;
                bScore = Math.min(100, Math.max(0, (1 - overRatio) * 100));
            }

            // E 점수 계산
            double eScore = 100.0; // 지난달 데이터 없으면 기본 100점
            if (lastTotal > 0) {
                double erCurr = (double) currFixed / currTotal;
                double erLast = (double) lastFixed / lastTotal;
                eScore = Math.min(1.0, erCurr / erLast) * 100;
            }

            // 최종 점수
            double finalScore = w1 * bScore + w2 * eScore;
            monthlySavingScore.add(new MonthlySavingScoreDto(reports.get(i).getReportMonth(), finalScore));
        }

        return monthlySavingScore;
    }

    private Report createEmptyReport(Integer userId, LocalDate reportMonth) {
        Report report = new Report();
        report.setReportMonth(reportMonth);
        report.setMonthlyBudget(0L);
        report.setMonthlyTotalExpense(0L);
        // 다른 필드도 전부 0 또는 기본값
        return report;
    }

    public void save(Report report) {
        reportRepository.save(report);
    }
}
