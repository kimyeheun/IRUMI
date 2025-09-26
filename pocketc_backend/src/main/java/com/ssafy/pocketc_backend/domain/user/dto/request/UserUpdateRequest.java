package com.ssafy.pocketc_backend.domain.user.dto.request;

public record UserUpdateRequest(
        String name,
        Long budget
) {}
