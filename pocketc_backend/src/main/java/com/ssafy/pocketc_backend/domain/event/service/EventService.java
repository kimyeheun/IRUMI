package com.ssafy.pocketc_backend.domain.event.service;

import com.ssafy.pocketc_backend.domain.event.dto.response.*;
import com.ssafy.pocketc_backend.domain.event.entity.Event;
import com.ssafy.pocketc_backend.domain.event.entity.Room;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.event.repository.RoomRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.ssafy.pocketc_backend.domain.event.exception.EventErrorType.*;
import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.NOT_FOUND_MEMBER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;

    public RoomResDto getRoom(Principal principal){
        int userId = 1;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() == null) return new RoomResDto(null);
        return getRoomResDto(user.getRoom());
    }

    public RoomResDto joinRoom(String roomCode, Principal principal) {
        int userId = 1;

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ERROR_GET_ROOM));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() != null) throw new CustomException(ERROR_ALREADY_INCLUDED_ROOM);

        user.setRoom(room);
        return getRoomResDto(user.getRoom());
    }

    public RoomResDto createRoom(Integer maxMembers, Principal principal) {
        int userId = 1;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() != null) throw new CustomException(ERROR_ALREADY_INCLUDED_ROOM);

        Event event = eventRepository.findFirstByOrderByEventIdDesc();

        Room room = Room.builder()
                .event(event)
                .maxNumber(maxMembers)
                .build();

        roomRepository.save(room);
        user.setRoom(room);
        return getRoomResDto(user.getRoom());
    }

    public void leaveRoom(Principal principal) {
        int userId = 1;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() == null) throw new CustomException(ERROR_NOT_INCLUDED_ROOM);
        Room room = user.getRoom();
        user.setRoom(null);

        List<User> users = userRepository.findAllByRoom(room);
        if (users.isEmpty()) roomRepository.delete(room);
    }

    public MemberListDto getMembers(Principal principal) {

        int userId = 1;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() == null) throw new CustomException(ERROR_NOT_INCLUDED_ROOM);

        List<User> members = userRepository.findAllByRoom(user.getRoom());
        List<MemberDto> memberDtos = new ArrayList<>();
        for (User member : members) {
            if (member.getUserId() == userId) continue;
            memberDtos.add(MemberDto.of(
                    member.getUserId(),
                    member.getName(),
                    "ProfileImageUrl"
//                    member.getProfileImageUrl()
            ));
        }
        return new MemberListDto(memberDtos);
    }

    private RoomResDto getRoomResDto(Room room) {
        EventResDto eventResDto = EventResDto.from(room.getEvent());
        RoomDetailDto roomDetailDto = RoomDetailDto.of(room.getRoomId(), room.getMaxNumber(), room.getCreatedAt(), String.valueOf(room.getStatus()), room.getRoomCode(), eventResDto);
        return new RoomResDto(roomDetailDto);
    }
}