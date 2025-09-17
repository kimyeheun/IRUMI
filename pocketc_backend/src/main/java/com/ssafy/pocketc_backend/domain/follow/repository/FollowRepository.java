package com.ssafy.pocketc_backend.domain.follow.repository;

import com.ssafy.pocketc_backend.domain.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 1. 팔로우 목록 조회
 */
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    boolean existsByFollower_UserIdAndFollowee_UserId(Integer followerId, Integer followedId);
    void deleteByFollower_UserIdAndFollowee_UserId(Integer followerId, Integer followedId);
    List<Follow> findAllByFollower_UserId(Integer userId);
}
