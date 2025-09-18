package com.ssafy.pocketc_backend.domain.event.dto.response;

import com.ssafy.pocketc_backend.domain.event.entity.Puzzle;

public record PuzzleDto(
        Integer pieceId,
        Integer row,
        Integer column,
        Integer userId
) {
    public static PuzzleDto from(Puzzle puzzle) {
        return new PuzzleDto(
                puzzle.getPuzzleId(),
                puzzle.getRowIndex(),
                puzzle.getColumnIndex(),
                puzzle.getUser().getUserId()
        );
    }
}
