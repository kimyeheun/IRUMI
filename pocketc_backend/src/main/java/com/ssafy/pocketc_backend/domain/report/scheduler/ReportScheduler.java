package com.ssafy.pocketc_backend.domain.report.scheduler;

import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Report 생성 스케줄러. 매월 1일 00시 10분 모든 유저에게 report를 생성하는 이벤트 스케줄러.
 * https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-cron-expression : 스케줄링 레퍼런스
 */
@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    // 매월 1일 00:10에 각 user에게 report를 준다.
    @Scheduled(cron = "0 10 0 1 * ?")
    public void createMonthlyReports() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        // 모든 사용자 조회
        List<User> users = userRepository.findAll();

        // 사용자별 Report 생성 (이미 생성된 건 스킵)
        for (User user : users) {
            boolean exists = reportRepository
                    .findByUser_UserIdAndReportMonth(user.getUserId(), currentMonth)
                    .isPresent();

            if (!exists) {
                Report report = new Report();
                report.setUser(user);
                report.setReportMonth(currentMonth);
                report.setMonthlyTotalExpense(0L);
                report.setMonthlyFixedExpense(0L);
                report.setMonthlyBudget(user.getBudget());

                reportRepository.save(report);
            }
        }
    }
}
