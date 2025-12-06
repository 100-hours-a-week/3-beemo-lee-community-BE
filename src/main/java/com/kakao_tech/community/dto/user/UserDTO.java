package com.kakao_tech.community.dto.user;

import com.kakao_tech.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private Integer userId;
        private String nickname;
        private String email;
        private String profileUrl;

        public static Response from(User user) {
            return Response.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .profileUrl(user.getFullProfileUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String nickname;
        private String profileUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateResponse {
        private Integer userId;
        private String nickname;
        private String profileUrl;
        private String message;

        public static UpdateResponse from(User user) {
            return UpdateResponse.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileUrl(user.getFullProfileUrl())
                    .message("회원 정보가 수정되었어요.")
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordUpdateRequest {
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PasswordUpdateResponse {
        private String message;
    }
}