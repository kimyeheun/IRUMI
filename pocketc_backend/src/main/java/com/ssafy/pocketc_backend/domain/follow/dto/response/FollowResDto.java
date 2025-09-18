package com.ssafy.pocketc_backend.domain.follow.dto.response;

import com.ssafy.pocketc_backend.domain.follow.entity.Follow;

import java.time.LocalDateTime;

public record FollowResDto(
        Integer followeeId,
        LocalDateTime followedAt
) {
    public static FollowResDto from(Follow follow) {
        return new FollowResDto(
                follow.getFollowee().getUserId(),
                follow.getCreatedAt()
        );
    }
}
