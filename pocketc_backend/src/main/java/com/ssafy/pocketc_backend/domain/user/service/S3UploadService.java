package com.ssafy.pocketc_backend.domain.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cloud.aws.s3.enabled", havingValue = "true", matchIfMissing = true)
public class S3UploadService {

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucket;

    /**
     * 이미지 파일을 저장한다.
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, originalFileName, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, originalFileName).toString();
    }

    /**
     * 파일을 삭제한다.
     */
    public void deleteImage(String originalFileName) {
        amazonS3.deleteObject(bucket, originalFileName);
    }

    /*TODO 조회 기능 추가*/
}
