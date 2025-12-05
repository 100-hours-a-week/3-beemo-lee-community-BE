package com.kakao_tech.community.dto.user;

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