package com.kakao_tech.community.controller;

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

    @PostMapping("")
    public ResponseEntity<CommentDTO.CreateResponse> createComment(
            @PathVariable("postId") Long postId,
            @RequestAttribute("userId") Integer userId,
            @RequestBody CommentDTO.CreateRequest request) {

        CommentDTO.CreateResponse response = commentService.createComment(
                request.getBody(),
                userId,
                postId);

        return ResponseEntity.status(201).body(response);
    }

}
