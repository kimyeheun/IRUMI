package com.ssafy.pocketc_backend.domain.follow.controller;

import com.ssafy.pocketc_backend.domain.follow.dto.response.FollowListResDto;
import com.ssafy.pocketc_backend.domain.follow.service.FollowService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.follow.exception.FollowSuccessType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class FollowController {

    private final FollowService followService;

//    private Integer userId(Principal principal) {
//        return Integer.parseInt(principal.getName());
//    }

    @PostMapping("{userId}/follows/{targetUserId}")
    public ResponseEntity<ApiResponse<?>> follow(@PathVariable Integer userId, @PathVariable Integer targetUserId) {
        followService.follow(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_POST_FOLLOW));
    }

    @DeleteMapping("{userId}/follows/{targetUserId}")
    public ResponseEntity<ApiResponse<?>> unfollow(@PathVariable Integer userId, @PathVariable Integer targetUserId) {
        followService.unfollow(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_DELETE_FOLLOW));
    }

    @GetMapping("{userId}/follows")
    public ResponseEntity<ApiResponse<FollowListResDto>> getFollowList(@PathVariable Integer userId) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_FOLLOWS,
                followService.getFollowList(userId)
        ));
    }
}
