package com.kakao_tech.community.controller;

import com.kakao_tech.community.common.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao_tech.community.dto.CommentDTO;
import com.kakao_tech.community.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<CommentDTO.ListResponse>> getComments(
            @PathVariable("postId") Long postId) {
        CommentDTO.ListResponse response = commentService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CommentDTO.CreateResponse>> createComment(
            @PathVariable("postId") Long postId,
            @RequestAttribute("userId") Integer userId,
            @RequestBody CommentDTO.CreateRequest request) {

        CommentDTO.CreateResponse response = commentService.createComment(
                request.getBody(),
                userId,
                postId);

        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO.UpdateResponse>> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestAttribute("userId") Integer userId,
            @RequestBody CommentDTO.UpdateRequest request) {

        CommentDTO.UpdateResponse response = commentService.updateComment(commentId, request.getBody(), userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentId") Long commentId,
            @RequestAttribute("userId") Integer userId) {

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }
}
