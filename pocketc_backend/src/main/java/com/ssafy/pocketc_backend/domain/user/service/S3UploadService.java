package com.ssafy.pocketc_backend.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ssafy.pocketc_backend.global.util.ImageResizeUtil;
import com.ssafy.pocketc_backend.global.util.S3PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@ConditionalOnProperty(name = "spring.cloud.s3.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucket;

    /**
     * 이미지 파일을 저장한다.
     */
    public String saveFile(MultipartFile multipartFile, Integer userId) throws IOException {
        // 리사이즈
        byte[] resizedBytes = ImageResizeUtil.resizeImage(multipartFile);

        // 키 생성 (확장자는 jpg 고정)
        String key = S3PathUtil.profileImageKey(userId);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resizedBytes.length);
        metadata.setContentType("image/jpeg");

        amazonS3.putObject(bucket, key, new ByteArrayInputStream(resizedBytes), metadata);
        //url에 캐시 무효화 쿼리 파라미터 추가, 이미지 경로 고정시 생기는 브라우저 캐싱문제 해결
        long timestamp = System.currentTimeMillis();
        return amazonS3.getUrl(bucket, key).toString() + "?v=" + timestamp;
    }

    /**
     * 파일을 삭제한다.
     */
    public void deleteImage(String originalFileName) {
        amazonS3.deleteObject(bucket, originalFileName);
    }

    /*TODO 조회 기능 추가*/
}
