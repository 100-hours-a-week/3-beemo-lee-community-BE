package com.kakao_tech.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    // 이미지 검증 메서드
    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");
        }

        // 파일 크기 검증 (10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (image.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하여야 합니다.");
        }

        // MIME 타입 검증
        String contentType = image.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다. (JPG, JPEG, PNG, GIF, WebP만 가능)");
        }
    }

    // 허용된 이미지 MIME 타입 확인
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    // 프로필 이미지 업로드 (UUID 파일명)
    public String uploadProfileImage(MultipartFile image) {
        // 이미지 검증 (MIME 타입, 파일 크기)
        validateImage(image);

        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuidFilename = "profile-" + UUID.randomUUID() + extension;
        String s3Key = "public/users/profile/" + uuidFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            return s3Key;
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    // 게시글 이미지 업로드 (UUID 파일명)
    public String uploadPostImage(MultipartFile image) {
        // 이미지 검증 (MIME 타입, 파일 크기)
        validateImage(image);

        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuidFilename = "post-" + UUID.randomUUID() + extension;
        String s3Key = "public/posts/" + uuidFilename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(image.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            return s3Key;
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    // S3에서 이미지 다운로드
    public byte[] downloadImage(String s3Key) {
        try {
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build(),
                ResponseTransformer.toBytes()
            );

            return objectBytes.asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("S3 이미지 조회 실패: " + s3Key, e);
        }
    }

    // S3 객체의 Content-Type 조회
    public String getContentType(String s3Key) {
        try {
            GetObjectResponse response = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build(),
                ResponseTransformer.toBytes()
            ).response();

            return response.contentType();
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}
