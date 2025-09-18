package com.ssafy.pocketc_backend.domain.event.repository;

import com.ssafy.pocketc_backend.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Integer> {
    Event findFirstByOrderByEventIdDesc();
}