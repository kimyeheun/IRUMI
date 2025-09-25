package com.ssafy.pocketc_backend.domain.user.service;

import com.ssafy.pocketc_backend.domain.mission.service.MissionService;
import com.ssafy.pocketc_backend.domain.report.entity.Report;
import com.ssafy.pocketc_backend.domain.report.service.ReportService;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserLoginRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserSignupRequest;
import com.ssafy.pocketc_backend.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserLoginResponse;
import com.ssafy.pocketc_backend.domain.user.dto.response.UserProfileResponse;
import com.ssafy.pocketc_backend.domain.user.entity.Streak;
import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.exception.UserErrorType;
import com.ssafy.pocketc_backend.domain.user.repository.StreakRepository;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import com.ssafy.pocketc_backend.global.auth.jwt.JwtProvider;
import com.ssafy.pocketc_backend.global.auth.service.RefreshTokenService;
import com.ssafy.pocketc_backend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ssafy.pocketc_backend.domain.user.exception.UserErrorType.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final ReportService reportService;
    private final StreakRepository streakRepository;
    private final MissionService missionService;

    private static final List<String> DEFAULT_PROFILE_IMAGES = List.of(
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default1.jpg",
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default2.jpg",
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default3.jpg",
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default4.jpg",
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default5.jpg",
            "https://irumi-s3.s3.ap-northeast-2.amazonaws.com/profile/default6.jpg"
    );

    private String getRandomDefaultProfileImage() {
        int index = ThreadLocalRandom.current().nextInt(DEFAULT_PROFILE_IMAGES.size());
        return DEFAULT_PROFILE_IMAGES.get(index);
    }

    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(UserErrorType.ALREADY_EXISTS);
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .budget(request.getBudget())
                .profileImageUrl(getRandomDefaultProfileImage()) // 기본 이미지 미리 설정
                .build();

        userRepository.save(user);

        Report report = new Report();
        report.setUser(user);
        report.setReportMonth(LocalDate.now().withDayOfMonth(1));
        report.setMonthlyTotalExpense(0L);
        report.setMonthlyFixedExpense(0L);
        report.setMonthlyBudget(user.getBudget());

        reportService.save(report);

        streakRepository.save(Streak.builder()
                .user(user)
                .date(LocalDate.now())
                .missionCompletedCount(0)
                .spentAmount(0L)
                .status(false)
                .build());

        missionService.getWeeklyMissions(user.getUserId());
        missionService.getMonthlyMissions(user.getUserId());
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

    public UserProfileResponse getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        return new UserProfileResponse(
                user.getUserId(),
                user.getName(),
                user.getBudget(),
                user.getProfileImageUrl()
        );
    }
    @Transactional
    public void updateUser(Integer userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorType.NOT_FOUND_MEMBER_ERROR));
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new CustomException(UserErrorType.ALREADY_EXISTS);
            }
            user.updateEmail(request.email());
        }
        user.updateName(request.name());
        user.updateBudget(request.budget());
        if (request.password() != null) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }
    }

    //db에 키 저장, 이미지 업로드에 문제가 생겨 이미지가 저장되지 못하고 db에 키만 저장되는 문제 방지
    @Transactional
    public void updateProfileImage(Integer userId, MultipartFile file, boolean delete) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));

        String profileUrl = user.getProfileImageUrl();
        if (delete) {
            //삭제 요청이 있으면 기본이미지로 덮어쓴다
            user.updateProfileImage(getRandomDefaultProfileImage());

        } else if (file != null && !file.isEmpty()) {
            //삭제요청이 없고 새 파일이 들어와 있다면 덮어쓰기
            try {
                profileUrl = s3UploadService.saveFile(file, userId);
            } catch (Exception e) {
                throw new CustomException(S3_UPLOAD_FAIL);
            }

            user.updateProfileImage(profileUrl);
        }


    }
}
