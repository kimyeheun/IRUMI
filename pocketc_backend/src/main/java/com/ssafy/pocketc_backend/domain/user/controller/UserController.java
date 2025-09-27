package com.ssafy.pocketc_backend.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.pocketc_backend.domain.user.dto.request.TokenReissueRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserLoginRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserSignupRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserResponse;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserProfileResponse;
import com.ssafy.pocketc_backend.domain.user.service.UserService;
import com.ssafy.pocketc_backend.global.auth.jwt.JwtProvider;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

import static com.ssafy.pocketc_backend.domain.user.exception.UserSuccessType.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "회원가입", description = "이름, 패스워드, 이메일, 에산")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody UserSignupRequest request) throws JsonProcessingException {

        return ResponseEntity.ok(ApiResponse.success(SIGNUP_MEMBER_SUCCESS, userService.signup(request)));
    }

    @Operation(summary = "로그인", description = "아메일,패스워드")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserLoginRequest request) {

        return ResponseEntity.ok(ApiResponse.success(LOGIN_SUCCESS, userService.login(request)
        ));
    }

    @Operation(summary = "토큰 재발급", description = "리프레시토큰 기반으로 토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<UserResponse>> reissue(@RequestBody TokenReissueRequest request) {
        UserResponse response = jwtProvider.reissueToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.success(REISSUE_SUCCESS, response));
    }

    @Operation(summary = "로그아웃", description = "액세스 토큰 기반으로 리프레시 토큰 삭제")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        userService.logout(authorizationHeader);
        return ResponseEntity.ok(ApiResponse.success(LOGOUT_SUCCESS));
    }

    @Operation(summary = "내 회원정보 조회", description = "회원정보(이름, 이메일, 프로필 이미지 등)를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(PROCESS_SUCCESS, userService.getProfile(userId)));
    }


    @Operation(summary = "회원정보 수정", description = "전달하지 않은 필드는 변경되지 않음 " +
            "수정 가능한 항목: 이름 예산"
    )
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<?>> updateUser(
            Principal principal,
            @RequestBody UserUpdateRequest request
    ) {
        Integer userId = Integer.valueOf(principal.getName());
        userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(UPDATE_MEMBER_SUCCESS));
    }

    @Operation(summary = "프로필 이미지 수정",
            description = """
                    - 업로드/수정: 이미지 파일을 포함하고, delete=false (혹은 생략)
                    - 삭제: 이미지 파일 없이, delete=true
                    """)
    @PatchMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<?>> updateProfileImage(
            Principal principal,
            @RequestPart("profileImage") MultipartFile profileImage,
            @RequestParam(value = "delete", required = false, defaultValue = "false") boolean delete
    ) throws IOException {
        Integer userId = Integer.valueOf(principal.getName());
        userService.updateProfileImage(userId, profileImage, delete);
        return ResponseEntity.ok(ApiResponse.success(UPLOAD_SUCCESS));
    }


}
