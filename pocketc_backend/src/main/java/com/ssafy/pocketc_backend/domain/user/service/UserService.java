package com.ssafy.pocketc_backend.domain.user.service;

import com.ssafy.pocketc_backend.domain.user.dto.request.UserLoginRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserSignupRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserLoginResponse;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.exception.UserErrorType;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.auth.JwtProvider;
import com.ssafy.pocketc_backend.global.auth.service.RefreshTokenService;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final S3UploadService s3UploadService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(UserErrorType.ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .budget(request.getBudget())
//                .profileImageUrl()
                .build();

        userRepository.save(user);
    }
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(LOGIN_FAIL));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(LOGIN_FAIL);
        }

        return jwtProvider.issueToken(user.getUserId(), user.getEmail());
    }
    //로그아웃
    @Transactional
    public void logout(String accessToken) {
            String token = accessToken.replace("Bearer ", "");
            Integer userId = jwtProvider.getUserIdFromJwt(token);
            refreshTokenService.delete(userId.toString());
        }
    //회원정보수정
    @Transactional
    public void updateUser(Integer userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND_MEMBER_ERROR));

        user.updateProfile(request.name(), request.email(), request.budget());
        if (request.password() != null) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }
    }
    //토큰 재발급

}
