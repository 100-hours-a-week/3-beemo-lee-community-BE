package com.kakao_tech.community.controller;

import com.kakao_tech.community.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // S3에서 이미지 가져와서 전달
    @GetMapping("/**")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) {
        // /images/public/users/profile/profile-uuid.jpg -> public/users/profile/profile-uuid.jpg
        String s3Key = request.getRequestURI().replace("/api/images/", "");

        try {
            byte[] imageBytes = imageService.downloadImage(s3Key);
            String contentType = imageService.getContentType(s3Key);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS)) // 브라우저 캐싱 1시간
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
