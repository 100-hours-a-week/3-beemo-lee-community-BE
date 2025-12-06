package com.kakao_tech.community.controller;

import com.kakao_tech.community.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakao_tech.community.dto.LikeDTO;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.service.LikeService;
import com.kakao_tech.community.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikeDTO.AddResponse>> addLike(
            @PathVariable Long postId,
            @RequestAttribute("userId") Integer userId) {

        User user = userService.getUser(userId);
        LikeDTO.AddResponse response = likeService.addLike(postId, user);

        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<LikeDTO.RemoveResponse>> removeLike(
            @PathVariable Long postId,
            @RequestAttribute("userId") Integer userId) {

        User user = userService.getUser(userId);
        LikeDTO.RemoveResponse response = likeService.removeLike(postId, user);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<LikeDTO.CheckResponse>> checkLike(
            @PathVariable Long postId,
            @RequestAttribute("userId") Integer userId) {

        User user = userService.getUser(userId);
        LikeDTO.CheckResponse response = likeService.isLikedByUser(postId, user);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
