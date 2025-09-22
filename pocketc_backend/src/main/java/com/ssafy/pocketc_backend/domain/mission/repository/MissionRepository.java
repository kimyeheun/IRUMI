package com.ssafy.pocketc_backend.domain.mission.repository;

import com.ssafy.pocketc_backend.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Integer> {
    // 오늘 00:00:00 <= validFrom < 내일 00:00:00
    List<Mission> findAllByUser_UserIdAndValidFromGreaterThanEqualAndValidFromLessThan(
            Integer userId,
            LocalDateTime startOfDay,
            LocalDateTime nextStartOfDay
    );
}