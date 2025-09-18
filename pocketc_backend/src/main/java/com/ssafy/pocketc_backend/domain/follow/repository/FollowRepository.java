package com.ssafy.pocketc_backend.domain.follow.repository;

import com.ssafy.pocketc_backend.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    boolean existsByFollower_UserIdAndFollowee_UserId(Integer userId, Integer targetUserId);

    void deleteByFollower_UserIdAndFollowee_UserId(Integer userId, Integer targetUserId);

    List<Follow> findAllByFollower_UserId(Integer userId);
}
