package com.ssafy.pocketc_backend.domain.user.controller;

import com.ssafy.pocketc_backend.domain.user.dto.request.UserLoginRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserSignupRequest;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserLoginResponse;
import com.ssafy.pocketc_backend.domain.user.service.UserService;
import com.ssafy.pocketc_backend.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ssafy.pocketc_backend.domain.user.exception.UserSuccessType.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "아이디, 패스워드, 이메일, 프로필이미지")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(PROCESS_SUCCESS));
    }
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
