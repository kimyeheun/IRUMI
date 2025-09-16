package com.ssafy.pocketc_backend.domain.user.service;

import com.ssafy.pocketc_backend.domain.user.dto.request.UserSignupRequest;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.exception.UserErrorType;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final S3UploadService s3UploadService;

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
}
