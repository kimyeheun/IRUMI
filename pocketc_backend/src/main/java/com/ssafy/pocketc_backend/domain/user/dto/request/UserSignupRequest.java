package com.ssafy.pocketc_backend.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserSignupRequest {
    private String name;
    private String email;
    private String password;
    private Long budget;
//    private MultipartFile profileImage;

}
