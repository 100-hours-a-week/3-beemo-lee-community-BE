package com.kakao_tech.community.controller;

import com.kakao_tech.community.common.response.ApiResponse;
import com.kakao_tech.community.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakao_tech.community.dto.PostDTO;
import com.kakao_tech.community.service.PostService;
import com.kakao_tech.community.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    public final PostService postService;
    public final UserService userService;

    @GetMapping
    // GET - '/posts?limit=10'
    // GET - '/posts?limit=10&offset=10'
    public ResponseEntity<ApiResponse<PostDTO.ListResponse>> getPosts(
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", required = false) Long offset) {
        PostDTO.ListResponse response = postService.getPosts(limit, offset);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDTO.DetailResponse>> getPost(@PathVariable Long postId) {
        PostDTO.DetailResponse response = postService.getPost(postId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
    

    // 게시글 생성
    // TODO: 사진 여러장 받고 처리할 수 있도록 추가 필요함.
    @PostMapping
    public ResponseEntity<ApiResponse<PostDTO.CreateResponse>> createPost(
            @RequestAttribute("userId") Integer userid,
            @RequestPart(value = "post") PostDTO.CreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        PostDTO.CreateResponse response = postService.createPost(
                request.getTitle(),
                request.getBody(),
                image,
                userService.getUser(userid));

        return ResponseEntity.status(201).body(ApiResponse.success("게시글이 작성되었습니다.", response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDTO.UpdateResponse>> updatePost(
            @PathVariable Long postId,
            @RequestAttribute("userId") Integer userId,
            @RequestPart(value = "post") PostDTO.UpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        User user = userService.getUser(userId);
        PostDTO.UpdateResponse response = postService.updatePost(
                postId,
                request.getTitle(),
                request.getBody(),
                image,
                user);

        return ResponseEntity.ok(ApiResponse.success("게시글이 수정되었습니다.", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @RequestAttribute("userId") Integer userId) {

        User user = userService.getUser(userId);
        postService.deletePost(postId, user);

        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다."));
    }
}
