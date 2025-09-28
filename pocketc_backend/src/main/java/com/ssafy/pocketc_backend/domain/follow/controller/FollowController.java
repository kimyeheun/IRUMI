package com.ssafy.pocketc_backend.domain.follow.controller;

import com.ssafy.pocketc_backend.domain.follow.dto.request.FollowReqDto;
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

    private Integer userId(Principal principal) { return Integer.parseInt(principal.getName()); }

    @PostMapping("/follows/{targetUserId}")
    public ResponseEntity<ApiResponse<?>> follow(Principal principal, @PathVariable Integer targetUserId) {
        followService.follow(userId(principal), targetUserId);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_POST_FOLLOW));
    }

    @DeleteMapping("/follows/{targetUserId}")
    public ResponseEntity<ApiResponse<?>> unfollow(Principal principal, @PathVariable Integer targetUserId) {
        followService.unfollow(userId(principal), targetUserId);
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_DELETE_FOLLOW));
    }

    @GetMapping("/follows")
    public ResponseEntity<ApiResponse<FollowListResDto>> getFollowList(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                SUCCESS_GET_FOLLOWS,
                followService.getFollowList(userId(principal))
        ));
    }

    @PostMapping("/follows")
    public ResponseEntity<ApiResponse<?>> getFollow(@RequestBody FollowReqDto dto, Principal principal) {
        followService.doFollow(dto.userCode(), userId(principal));
        return ResponseEntity.ok(ApiResponse.success(SUCCESS_POST_FOLLOW));
    }
}