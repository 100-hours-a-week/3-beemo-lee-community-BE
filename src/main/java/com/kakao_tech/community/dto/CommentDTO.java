package com.kakao_tech.community.dto;

import com.kakao_tech.community.entity.Comment;
import com.kakao_tech.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Author {
        private Integer id; // 작성자 PK 아이디
        private String nickname; // 작성자 닉네임
        private String profileUrl; // 작성자 프로필 이미지 주소

        public static Author from(User user) {
            return Author.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileUrl(user.getFullProfileUrl())
                    .build();
        }
    }

    // 댓글 조회
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Long id; // 댓글 PK 아이디
        private Author author; // 작성자 정보, 아이디, 닉네임, 프로필 이미지 주소
        private String body;
        private LocalDateTime createAt; // 댓글 작성일
        private LocalDateTime updateAt; // 댓글 마지막 수정일

        public static Response from(Comment comment) {
            return Response.builder()
                    .id(comment.getId())
                    .author(Author.from(comment.getUser()))
                    .body(comment.getBody())
                    .createAt(comment.getCreatedAt())
                    .updateAt(comment.getUpdatedAt())
                    .build();
        }
    }

    // 댓글 리스트 가져오기
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ListResponse {
        private List<Response> comments; // 댓글 조회 응답 리스트화.
        private Long commentsTotalCount; // 해당 포스트에 존재하는 총 댓글 갯수.
        private Integer commentsGetCount; // 리스트에 담긴 댓글 갯수.
    }

    // 댓글 작성 요청
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String body;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateResponse {
        private Long commentId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String body;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateResponse {
        private Long commentId;
        private String body;
    }
}
