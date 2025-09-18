package com.ssafy.pocketc_backend.domain.event.service;

import com.ssafy.pocketc_backend.domain.event.dto.response.*;
import com.ssafy.pocketc_backend.domain.event.entity.Puzzle;
import com.ssafy.pocketc_backend.domain.event.entity.Room;
import com.ssafy.pocketc_backend.domain.event.repository.EventRepository;
import com.ssafy.pocketc_backend.domain.event.repository.RoomRepository;
import com.ssafy.pocketc_backend.domain.follow.repository.FollowRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ssafy.pocketc_backend.domain.event.exception.EventErrorType.*;
import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.NOT_FOUND_MEMBER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final FollowRepository followRepository;

    public RoomResDto getRoom(Principal principal){
        int userId = Integer.parseInt(principal.getName());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        return getRoomResDto(user);
    }

    public RoomResDto joinRoom(String roomCode, Principal principal) {
        int userId = Integer.parseInt(principal.getName());

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new CustomException(ERROR_GET_ROOM));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() != null) throw new CustomException(ERROR_ALREADY_INCLUDED_ROOM);

        user.setRoom(room);
        return getRoomResDto(user);
    }

    public RoomResDto createRoom(Integer maxMembers, Principal principal) {
        int userId = Integer.parseInt(principal.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() != null) throw new CustomException(ERROR_ALREADY_INCLUDED_ROOM);

        Room room = Room.builder()
                .maxNumber(maxMembers)
                .build();

        roomRepository.save(room);
        user.setRoom(room);
        return getRoomResDto(user);
    }

    public EventResDto leaveRoom(Principal principal) {
        int userId = Integer.parseInt(principal.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        if (user.getRoom() == null) throw new CustomException(ERROR_NOT_INCLUDED_ROOM);
        Room room = user.getRoom();
        user.setRoom(null);

        List<User> users = userRepository.findAllByRoom(room);
        if (users.isEmpty()) roomRepository.delete(room);

        EventDto eventDto = EventDto.from(eventRepository.findFirstByOrderByEventIdDesc());
        return new EventResDto(eventDto);
    }

    private RoomResDto getRoomResDto(User user) {

        EventDto event = EventDto.from(eventRepository.findFirstByOrderByEventIdDesc());

        if (user.getRoom() == null) return new RoomResDto(null, event);

        List<User> members = userRepository.findAllByRoom(user.getRoom());
        List<MemberDto> memberDtos = new ArrayList<>();

        Map<Integer, Integer> map = new HashMap<>();

        for (User member : members) {
            boolean isFriend = followRepository.existsByFollower_UserIdAndFollowee_UserId(user.getUserId(), member.getUserId());
            memberDtos.add(MemberDto.of(member.getUserId(), member.getName(), "ProfileImageUrl", isFriend));
            map.put(member.getUserId(), 0);
        }

        List<Puzzle> puzzles = user.getRoom().getPuzzles();
        List<PuzzleDto> puzzleDtos = new ArrayList<>();

        for (Puzzle puzzle : puzzles) {
            puzzleDtos.add(PuzzleDto.from(puzzle));
            map.put(puzzle.getUser().getUserId(), map.get(puzzle.getUser().getUserId()) + 1);
        }

        List<int[]> tmp = new ArrayList<>();
        for (int key : map.keySet()) {
            tmp.add(new int[]{key, map.get(key)});
        }

        tmp.sort((a, b) -> Integer.compare(b[1], a[1]));

        List<RankDto> ranks = new ArrayList<>();
        int idx = 1, prev = 0;
        for (int[] A : tmp) {
            if (prev == A[1]) idx--;
            ranks.add(new RankDto(A[0], idx++, A[1]));
            prev = A[1];
        }

        Room room = user.getRoom();
        RoomDetailDto roomDetailDto = RoomDetailDto.of(
                room.getRoomId(),
                room.getCreatedAt(),
                room.getMaxNumber(),
                user.getPuzzleAttempts(),
                String.valueOf(room.getStatus()),
                room.getRoomCode(),
                puzzleDtos,
                ranks,
                memberDtos);

        return new RoomResDto(roomDetailDto, event);
    }
}