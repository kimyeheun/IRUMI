package com.ssafy.pocketc_backend.domain.user.repository;

import com.ssafy.pocketc_backend.domain.user.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StreakRepository extends JpaRepository<Streak, Integer> {
    List<Streak> findAllByUser_UserId(Integer userUserId);
    Streak findByUser_userIdAndDate(Integer userUserId, LocalDate date);
}
