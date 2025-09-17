package com.ssafy.pocketc_backend.domain.follow.dto.response;

import com.ssafy.pocketc_backend.domain.follow.entity.Follow;

import java.time.LocalDateTime;

public record FollowResDto (
        Integer followerId,
        LocalDateTime followedAt
) {
    public static FollowResDto from(Follow follow) {
        return new FollowResDto(
                follow.getFollower().getUserId(),
                follow.getCreatedAt()
        );
    }
}
