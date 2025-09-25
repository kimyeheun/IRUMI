package com.ssafy.pocketc_backend.domain.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.pocketc_backend.domain.event.entity.Badge;
import com.ssafy.pocketc_backend.domain.event.entity.Event;
import com.ssafy.pocketc_backend.domain.event.repository.BadgeRepository;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.main.dto.MainResponse;
import com.ssafy.pocketc_backend.domain.main.dto.StreakDto;
import com.ssafy.pocketc_backend.domain.main.dto.StreakResDto;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.domain.transaction.entity.Transaction;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.transaction.service.TransactionService;
import com.ssafy.pocketc_backend.domain.user.entity.Streak;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.StreakRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static com.ssafy.pocketc_backend.domain.main.exception.MainErrorType.ERROR_GET_BADGE;

@Service
@RequiredArgsConstructor
public class MainService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final StreakRepository streakRepository;
    private final TransactionService transactionService;
    private final BadgeRepository badgeRepository;
    private final EventRepository eventRepository;

    public MainResponse getDailyScoreAndTotal(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.plusDays(1).atStartOfDay();
        LocalDate lastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        //총지출
        long total = transactionRepository.getTotalSpending(userId, from, to);
        //변동비 총합
        long fixed = transactionRepository.getFixedSpending(userId, from, to);
        //report에서 지난달 데이터 가져오기
        Report lastReport = reportRepository.findByUser_UserIdAndReportMonth(userId, lastMonth)
                .orElse(null);
        //지난달 지출
        long lastTotal = (lastReport != null && lastReport.getMonthlyTotalExpense() != null)
                ? lastReport.getMonthlyTotalExpense() : 0;
        //지난달 변동비
        long lastFixed = (lastReport != null && lastReport.getMonthlyFixedExpense() != null)
                ? lastReport.getMonthlyFixedExpense() : 0;

        long dailyBudget = getDailyBudget(userId, today);
        long budgetScore = calcBudgetScore(total, dailyBudget);
        long efficiencyScore = calcEfficiencyScore(fixed, total, lastFixed, lastTotal);

        double w1 = 0.7, w2 = 0.3;
        long savingScore = (long) (w1 * budgetScore + w2 * efficiencyScore);

        return new MainResponse(savingScore, total);
    }

    //하루 평균 예산
    private Long getDailyBudget(Integer userId, LocalDate today) {
        // 유저별 예산 가져오기
        long monthlyBudget = userRepository.findById(userId)
                .map(user -> user.getBudget() != null ? user.getBudget() : 0)
                .orElse(0L);

        long daysInMonth = YearMonth.from(today).lengthOfMonth();
        return daysInMonth == 0 ? 0 : monthlyBudget / daysInMonth;
    }

    //예산 준수점수
    //1-(오늘 소비-하루 평균 에산)/하루 평균예산
    private Long calcBudgetScore(Long total, Long dailyBudget) {
        if (dailyBudget == 0) return 100L;
        double ratio = (double) (total - dailyBudget) / dailyBudget;
        double score = Math.min(100, (Math.max(0, (1 - ratio) * 100)));
        return (long) score;
    }

    //필수 비필수 비율
    //a/b
    //a:오늘 필수소비/오늘 총소비
    //b:지난달 필수소비/총소비
    private Long calcEfficiencyScore(Long fixed, Long total, Long lastFixed, Long lastTotal) {
        if (total == 0) return 100L;//소비가 0이면 100점으로 간주
        double ERtoday = (double) fixed / total;
        double ERlast = (double) lastFixed / lastTotal;
        double ratio = ERlast == 0 ? 1 : ERtoday / ERlast;  // 0으로 나누기 방지
        double score = Math.min(1, ratio) * 100;

        return (long) score;
    }

    // 최장 스트릭 반환
    public Integer getLongestStreak(Integer userId) {
        List<Streak> streaks = streakRepository.findAllByUser_UserId(userId);
        int longestStreak = 0;
        int temp = 0;
        for (Streak streak: streaks) {
            if (streak.isStatus()) {
                temp++;
                longestStreak = Math.max(longestStreak, temp);
            } else {
                temp = 0;
            }
        }
        return longestStreak;
    }

    public StreakResDto getStreaks(Integer userId) {
        List<Streak> streaks = streakRepository.findAllByUser_UserId(userId);
        List<StreakDto> streakDtos = new ArrayList<>();
        for (Streak streak : streaks) {
            streakDtos.add(StreakDto.from(streak));
        }
        return new StreakResDto(streakDtos);
    }

    /**
     * 새벽 6시에 모든 유저의 당일 결제내역을 반영합니다.
     * 반영하는 결제일 : 현재 일자 기준 전날 0600i ~ 오늘일자 0600i
     * 결제내역 반영 후 스트릭 추가
     */
    public void applyDailyTransaction() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            // 유저의 당일 결제내역 조회
            List<Transaction> transactions = transactionRepository.findAllByUser_UserIdAndTransactedAtGreaterThanEqualAndTransactedAtLessThan(
                    user.getUserId(),
                    now.minusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0),
                    now.withHour(6).withMinute(0).withSecond(0).withNano(0)
            );

            // 결제내역 반영 및 당일 지출 금액, 성공한 미션 갯수 count
            int successCount = 0;
            for (Transaction transaction : transactions) {
                successCount += transactionService.appliedTransaction(transaction.getTransactionId(), user.getUserId());
            }

            // 스트릭 생성
            Streak streak = streakRepository.findByUser_userIdAndDate(user.getUserId(), now.minusDays(1).toLocalDate());
            streak.setMissionCompletedCount(successCount);
            streak.setStatus(successCount > 0);

            // 성공한 미션 갯수만큼 퍼즐 부여
            user.setPuzzleAttempts(successCount);

            // 스트릭 수에 따라 뱃지 부여
            int longestStreak = getLongestStreak(user.getUserId());
            if (longestStreak > 0 && (longestStreak & (longestStreak - 1)) == 0) {

                int badgeIndex = (int) (Math.log(longestStreak) / Math.log(2));

                Event event = eventRepository.findById(badgeIndex)
                        .orElseThrow(() -> new CustomException(ERROR_GET_BADGE));

                badgeRepository.save(Badge.builder()
                                .user(user)
                                .event(event)
                                .level(1)
                        .build());
            }
        }
    }

    public void createNewStreak() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            streakRepository.save(Streak.builder()
                            .user(user)
                            .date(LocalDate.now())
                            .missionCompletedCount(0)
                            .spentAmount(0L)
                            .status(false)
                    .build());
        }
    }
}