package com.ssafy.pocketc_backend.domain.follow.dto.request;

import java.security.Principal;

public record FollowReqDto(
        Principal principal,
        Integer targetUserId
) {
}
