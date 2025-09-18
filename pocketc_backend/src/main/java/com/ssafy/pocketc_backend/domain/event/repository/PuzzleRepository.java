package com.ssafy.pocketc_backend.domain.event.repository;

import com.ssafy.pocketc_backend.domain.event.entity.Puzzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, Integer> {
}