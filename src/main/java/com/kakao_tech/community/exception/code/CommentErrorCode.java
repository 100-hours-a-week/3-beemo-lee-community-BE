package com.kakao_tech.community.exception.code;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.http.HttpStatus;

import com.kakao_tech.community.exception.common.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    // 존재하지 않은 리소스
    INVALID_COMMENT_ID(BAD_REQUEST, "INVALID_COMMENT_ID", "댓글이 존재하지 않아요."),

    // 입력값 부재
    REQUIRED_COMMENT_CONTENT(BAD_REQUEST, "REQUIRED_COMMENT_CONTENT", "댓글 내용을 입력해주세요."),

    // 권한 오류
    FORBIDDEN_COMMENT_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_COMMENT_ACCESS", "댓글에 대한 권한이 없어요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
