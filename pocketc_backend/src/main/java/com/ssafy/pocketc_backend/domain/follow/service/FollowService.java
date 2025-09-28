package com.ssafy.pocketc_backend.domain.follow.service;

import com.ssafy.pocketc_backend.domain.follow.dto.response.FollowListResDto;
import com.ssafy.pocketc_backend.domain.follow.dto.response.FollowResDto;
import com.ssafy.pocketc_backend.domain.follow.entity.Follow;
import com.ssafy.pocketc_backend.domain.follow.repository.FollowRepository;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ssafy.pocketc_backend.domain.follow.exception.FollowErrorType.ERROR_GET_NOT_FOUND_USER;
import static com.ssafy.pocketc_backend.domain.follow.exception.FollowErrorType.ERROR_POST_SELF_FOLLOW;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(Integer userId, Integer targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new CustomException(ERROR_POST_SELF_FOLLOW);
        }

        if (followRepository.existsByFollower_UserIdAndFollowee_UserId(userId, targetUserId)) {
            return;
        }

        if (!userRepository.existsById(targetUserId)) {
            /*TODO ERROR HANDLING*/
            throw new CustomException(ERROR_GET_NOT_FOUND_USER);
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

    public FollowListResDto buildFollowListDto(List<Follow> follows) {
        List<FollowResDto> followResDtoList = new ArrayList<>();

        for (Follow follow : follows) followResDtoList.add(FollowResDto.from(follow));

        return FollowListResDto.of(followResDtoList);
    }

    public void doFollow(String userCode, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ERROR_GET_NOT_FOUND_USER));

        User follower = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new CustomException(ERROR_GET_NOT_FOUND_USER));

        Follow follow = Follow.builder()
                .follower(user)
                .followee(follower)
                .build();
        followRepository.save(follow);
    }
}