package com.kakao_tech.community.dto;

import com.kakao_tech.community.entity.Post;
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

        public static AddResponse of(Post post, String message) {
            return AddResponse.builder()
                    .message(message)
                    .postId(post.getId())
                    .likesCnt(post.getLikesCnt())
                    .build();
        }
    }

    // 좋아요 삭제 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class RemoveResponse {
        private String message;
        private Long postId;
        private Integer likesCnt; // 현재 좋아요 개수

        public static RemoveResponse of(Post post, String message) {
            return RemoveResponse.builder()
                    .message(message)
                    .postId(post.getId())
                    .likesCnt(post.getLikesCnt())
                    .build();
        }
    }

    // 좋아요 여부 확인 응답
    @Getter
    @Builder
    @AllArgsConstructor
    public static class CheckResponse {
        private Long postId;
        private Boolean isLiked;
        private Integer likesCnt; // 현재 좋아요 개수

        public static CheckResponse of(Post post, boolean isLiked) {
            return CheckResponse.builder()
                    .postId(post.getId())
                    .isLiked(isLiked)
                    .likesCnt(post.getLikesCnt())
                    .build();
        }
    }
}
