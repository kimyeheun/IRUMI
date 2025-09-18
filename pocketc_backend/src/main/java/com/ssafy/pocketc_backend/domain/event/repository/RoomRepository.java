package com.ssafy.pocketc_backend.domain.event.repository;

import com.ssafy.pocketc_backend.domain.event.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {
    Optional<Room> findByRoomCode(String roomCode);
}
