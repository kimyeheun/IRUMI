package com.ssafy.pocketc_backend.domain.event.dto.response;

public record RankDto(
        Integer userId,
        Integer rank,
        Integer count
) {
    public static RankDto of(Integer userId, Integer rank, Integer count) {
        return new RankDto(userId, rank, count);
    }
}
