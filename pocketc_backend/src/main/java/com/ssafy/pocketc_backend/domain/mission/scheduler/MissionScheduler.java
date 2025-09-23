package com.ssafy.pocketc_backend.domain.mission.scheduler;

import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final MissionService missionService;

    //매주 월요일 00시 실행
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void doWeeklyJobs() {
        missionService.assignWeeklyMissions();
    }

    //매달 1일 00시 실행
    @Scheduled(cron = "0 0 0 1 * ?", zone = "Asia/Seoul")
    public void doMonthlyJobs() {
        missionService.assignMonthlyMissions();
    }
}
