package com.ssafy.pocketc_backend.global.data;

import com.ssafy.pocketc_backend.domain.event.entity.Event;
import com.ssafy.pocketc_backend.domain.event.repository.BadgeRepository;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.event.repository.RoomRepository;
import com.ssafy.pocketc_backend.domain.event.service.EventService;
import com.ssafy.pocketc_backend.domain.follow.repository.FollowRepository;
import com.ssafy.pocketc_backend.domain.mission.repository.MissionRepository;
import com.ssafy.pocketc_backend.domain.mission.service.MissionRedisService;
import com.ssafy.pocketc_backend.domain.report.repository.ReportRepository;
import com.ssafy.pocketc_backend.domain.transaction.repository.TransactionRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.StreakRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DataService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final FollowRepository followRepository;
    private final MissionRepository missionRepository;
    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final StreakRepository streakRepository;
    private final EventRepository eventRepository;

    private final MissionRedisService missionRedisService;
    private final EventService eventService;

    private final DataSource dataSource;
    private final JdbcTemplate jdbc;

    public void deleteAll() {
//        List<User> users = userRepository.findAll();
//        for (User user : users) {
//            if (user.getRoom() != null)
//                eventService.leaveRoom(user.getUserId());
//        }
//
//        badgeRepository.deleteAll();
//        followRepository.deleteAll();
//        missionRepository.deleteAll();
//        reportRepository.deleteAll();
//        transactionRepository.deleteAll();
//        streakRepository.deleteAll();
//        eventRepository.deleteAll();
//        userRepository.deleteAll();

        jdbc.execute("SET FOREIGN_KEY_CHECKS=0");

        for (String t : new String[]{
                "streaks","transactions","follows","missions",
                "reports","badges","rooms","events","users","puzzles"
        }) {
            jdbc.execute("TRUNCATE TABLE " + t);
        }
        jdbc.execute("SET FOREIGN_KEY_CHECKS=1");
        missionRedisService.deleteAll();

        missionRedisService.deleteAll();
    }

    public void deleteLastEvent() {
        Event firstByOrderByEventIdDesc = eventRepository.findFirstByOrderByEventIdDesc();
        eventRepository.delete(firstByOrderByEventIdDesc);
    }

    public void putAllData() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/init_v2.sql"));
        populator.setContinueOnError(false);
        populator.setSeparator(";");
        populator.execute(dataSource);
    }
}