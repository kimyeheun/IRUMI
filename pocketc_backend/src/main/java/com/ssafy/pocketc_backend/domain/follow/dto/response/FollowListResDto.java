package com.ssafy.pocketc_backend.domain.follow.dto.response;

import com.ssafy.pocketc_backend.domain.follow.entity.Follow;

import java.util.List;

public record FollowListResDto (
        List<Follow> follows
) { }
