package com.ssafy.pocketc_backend.domain.event.service;

import com.ssafy.pocketc_backend.domain.event.dto.response.EventResDto;
import com.ssafy.pocketc_backend.domain.event.dto.response.RoomDetailDto;
import com.ssafy.pocketc_backend.domain.event.dto.response.RoomResDto;
import com.ssafy.pocketc_backend.domain.event.entity.Room;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RoomResDto getRoom(Principal principal){
        int userId = 1;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRoom() == null) return new RoomResDto(null);
        Room room = user.getRoom();
        EventResDto eventResDto = EventResDto.from(room.getEvent());
        RoomDetailDto roomDetailDto = RoomDetailDto.of(room.getRoomId(), room.getMaxNumber(), room.getCreatedAt(), String.valueOf(room.getStatus()), room.getRoomCode(), eventResDto);
        return new RoomResDto(roomDetailDto);
    }
}