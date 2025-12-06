package com.kakao_tech.community.exception.handler;

import com.kakao_tech.community.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakao_tech.community.exception.common.ErrorCode;
import com.kakao_tech.community.exception.common.RestApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ApiResponse<Void>> restApiExceptionHandler(RestApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getMessage()));
    }

    // Vaild 어노테이션 검증 실패시 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // TODO: 에러 코드를 분석해서 적절한 AuthErrorCode 매핑
        // 상세 에러 메시지를 e.getBindingResult()에서 꺼낼 수도 있음
        return ResponseEntity
                .status(400)
                .body(ApiResponse.error("입력값이 올바르지 않습니다. (Validation Error)"));
    }

    // IllegalArgumentException 처리 (이미지 검증 실패 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(400)
                .body(ApiResponse.error(e.getMessage()));
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        e.printStackTrace(); // 로그 출력
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error("서버 오류가 발생했습니다: " + e.getMessage()));
    }
}
