package com.kakao_tech.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Integer id;

    // S3 키 (예: public/users/profile/profile-uuid.jpg)
    @Column(unique = true, nullable = false)
    private String s3Key;

    // 원본 파일명
    @Column(nullable = true)
    private String originalFilename;

    @Column(unique = false, nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(unique = false, nullable = true, columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "INT")
    private User user;

    protected Image() {}

    public Image(String s3Key, String originalFilename, User user) {
        this.s3Key = s3Key;
        this.originalFilename = originalFilename;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
