package com.ssafy.pocketc_backend.global.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class MissionTestController {

    private final MissionService missionService;

    /** 주간 미션 강제 실행 */
    @PostMapping("/weekly")
    public String runWeeklyMissions() throws JsonProcessingException {
        long start = System.currentTimeMillis();
        missionService.assignWeeklyMissions();
        long end = System.currentTimeMillis();
        return "Weekly missions executed in " + (end - start) + " ms";
    }

    /** 월간 미션 강제 실행 */
    @PostMapping("/monthly")
    public String runMonthlyMissions() throws JsonProcessingException {
        long start = System.currentTimeMillis();
        missionService.assignMonthlyMissions();
        long end = System.currentTimeMillis();
        return "Monthly missions executed in " + (end - start) + " ms";
    }
}
