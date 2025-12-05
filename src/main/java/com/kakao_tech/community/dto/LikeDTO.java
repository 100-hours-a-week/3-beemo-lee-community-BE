package com.kakao_tech.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class LikeDTO {

    // 좋아요 추가 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class AddResponse {
        private String message;
        private Long postId;
        private Integer likesCnt; // 현재 좋아요 개수
    }

    // 좋아요 삭제 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class RemoveResponse {
        private String message;
        private Long postId;
        private Integer likesCnt; // 현재 좋아요 개수
    }

    // 좋아요 여부 확인 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CheckResponse {
        private Long postId;
        private Boolean isLiked;
        private Integer likesCnt; // 현재 좋아요 개수
    }
}
