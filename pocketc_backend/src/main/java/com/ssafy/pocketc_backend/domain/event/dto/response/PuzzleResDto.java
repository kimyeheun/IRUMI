package com.ssafy.pocketc_backend.domain.event.dto.response;

import java.util.List;

public record PuzzleResDto(
        List<PuzzleDto> puzzles,
        List<RankDto> ranks,
        Integer puzzleAttempts
) {}