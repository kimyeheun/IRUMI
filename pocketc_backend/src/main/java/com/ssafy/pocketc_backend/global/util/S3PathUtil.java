package com.ssafy.pocketc_backend.global.util;

import java.util.UUID;

public class S3PathUtil {
    public static String profileImageKey(Integer userId) {
        //프사는 덮어쓰기 되도록 경로 고정(한 유저당 하나의 이미지만 사용하고, 이전 이미지 조회할 필요없음)
        return "profile/" + userId + "/profile.jpg";
    }
}