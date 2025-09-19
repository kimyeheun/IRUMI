package com.ssafy.pocketc_backend.domain.user.dto.request;

public record UserUpdateRequest(
//    String profileImage,
    String name,
    String email,
    String password,
    Long budget
) {}
