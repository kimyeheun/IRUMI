package com.ssafy.pocketc_backend.domain.user.repository;

import com.ssafy.pocketc_backend.domain.event.entity.Room;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findAllByRoom(Room room);
    Optional<User> findByUserCode(String userCode);
}
