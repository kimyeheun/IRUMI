package com.ssafy.pocketc_backend.domain.event.repository;

import com.ssafy.pocketc_backend.domain.event.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge,Integer> {
    List<Badge> findAllByUser_UserId(Integer userUserId);
}