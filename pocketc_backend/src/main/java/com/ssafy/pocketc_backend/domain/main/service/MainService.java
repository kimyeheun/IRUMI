package com.ssafy.pocketc_backend.domain.main.service;

import com.ssafy.pocketc_backend.domain.main.dto.MainResponse;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class MainService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public MainResponse getDailyScoreAndTotal(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();
        LocalDate lastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        //총지출
        int total = transactionRepository.getTotalSpending(userId, from, to);
        //변동비 총합
        int fixed = transactionRepository.getFixedSpending(userId, from, to);
        //report에서 지난달 데이터 가져오기
        Report lastReport = reportRepository.findByUser_UserIdAndReportMonth(userId, lastMonth)
                .orElse(null);
        //지난달 지출
        int lastTotal = (lastReport != null && lastReport.getMonthlyTotalExpense() != null)
                ? lastReport.getMonthlyTotalExpense() : 0;
        //지난달 변동비
        int lastFixed = (lastReport != null && lastReport.getMonthlyFixedExpense() != null)
                ? lastReport.getMonthlyFixedExpense() : 0;
        int dailyBudget = getDailyBudget(userId,today);
        int budgetScore = calcBudgetScore(total,dailyBudget);
        int efficiencyScore = calcEfficiencyScore(fixed, total, lastFixed, lastTotal);

        double w1 = 0.5, w2 = 0.5;//우선 5대5로 설정
        int savingScore = (int)(w1 * budgetScore + w2 * efficiencyScore);


        return new MainResponse(savingScore,total);

    }

    //하루 평균 예산
    private int getDailyBudget(Integer userId, LocalDate today) {
        // 유저별 예산 가져오기
        int monthlyBudget = userRepository.findById(userId)
                .map(user -> user.getBudget() != null ? user.getBudget() : 0)
                .orElse(0);

        int daysInMonth = YearMonth.from(today).lengthOfMonth();
        return daysInMonth == 0 ? 0 : monthlyBudget / daysInMonth;
    }
    //예산 준수점수
    //1-(오늘 소비-하루 평균 에산)/하루 평균예산
    private int calcBudgetScore(int total, int dailyBudget) {
        if (dailyBudget == 0) return 100;
        double ratio = (double)(total-dailyBudget)/dailyBudget;
        double score = Math.min(100,(Math.max(0,(1-ratio)*100)));
        return (int) score;
    }
    //필수 비필수 비율
    //a/b
    //a:오늘 필수소비/오늘 총소비
    //b:지난달 필수소비/총소비
    private int calcEfficiencyScore(int fixed, int total, int lastFixed, int lastTotal) {
        if (total == 0) return 100;//소비가 0이면 100점으로 간주
        double ERtoday = (double) fixed / total;
        double ERlast = (double) lastFixed/lastTotal;
        double ratio = ERlast == 0 ? 1 : ERtoday / ERlast;  // 0으로 나누기 방지
        double score = Math.min(1, ratio) * 100;

        return (int) score;
    }


}