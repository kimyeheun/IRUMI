package com.ssafy.pocketc_backend;

import com.ssafy.pocketc_backend.domain.user.entity.User;
import com.ssafy.pocketc_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            User devUser = User.builder()
                    .email("dev@test.com")
                    .name("개발자")
                    .password("nopass")
                    .build();
            userRepository.save(devUser);
            System.out.println("개발자용 유저 생성: userId=1");
        }
    }
}

