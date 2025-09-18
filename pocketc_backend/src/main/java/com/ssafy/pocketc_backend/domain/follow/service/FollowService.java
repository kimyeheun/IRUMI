package com.ssafy.pocketc_backend.domain.follow.service;

import com.ssafy.pocketc_backend.domain.follow.dto.response.FollowListResDto;
import com.ssafy.pocketc_backend.domain.follow.dto.response.FollowResDto;
import com.ssafy.pocketc_backend.domain.follow.entity.Follow;
import com.ssafy.pocketc_backend.domain.follow.repository.FollowRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Integer userId, Integer targetUserId) {
        if (userId.equals(targetUserId)) {
            /*TODO ERROR HANDLING*/
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }

        if (followRepository.existsByFollower_UserIdAndFollowee_UserId(userId, targetUserId)) {
            return;
        }

        if (!userRepository.existsById(targetUserId)) {
            /*TODO ERROR HANDLING*/
            throw new IllegalArgumentException("존재하지 않는 사용자입니다. : " + targetUserId);
        }

        User followerRef = userRepository.getReferenceById(userId);
        User followeeRef = userRepository.getReferenceById(targetUserId);

        Follow follow = new Follow(null, followerRef, followeeRef);
        followRepository.save(follow);
    }

    @Transactional
    public void unfollow(Integer userId, Integer targetUserId) {
        followRepository.deleteByFollower_UserIdAndFollowee_UserId(userId, targetUserId);
    }

    public FollowListResDto getFollowList(Integer userId) {
        List<Follow> follows = followRepository.findAllByFollower_UserId(userId);

        return buildFollowListDto(follows);
    }

    public FollowListResDto buildFollowListDto (List<Follow> follows) {
        List<FollowResDto> followResDtoList = new ArrayList<>();

        for (Follow follow : follows) followResDtoList.add(FollowResDto.from(follow));

        return FollowListResDto.of(followResDtoList);
    }
}
