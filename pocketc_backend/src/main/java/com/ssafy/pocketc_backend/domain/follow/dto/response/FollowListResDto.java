package com.ssafy.pocketc_backend.domain.follow.dto.response;

import java.util.List;

public record FollowListResDto (
        List<FollowResDto> follows
){
    public static FollowListResDto of(List<FollowResDto> follows) {
        return new FollowListResDto(follows);
    }
}
