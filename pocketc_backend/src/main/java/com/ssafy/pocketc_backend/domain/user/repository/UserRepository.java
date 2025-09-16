package com.ssafy.pocketc_backend.domain.user.repository;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByEmail(String email);
}
