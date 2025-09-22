package com.ssafy.pocketc_backend.domain.mission.repository;

import com.ssafy.pocketc_backend.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Integer> {
    List<Mission> findAllByUser_UserId(Integer userId);
}