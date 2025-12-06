package com.kakao_tech.community.service;

import com.kakao_tech.community.exception.code.AuthErrorCode;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kakao_tech.community.dto.PostDTO;
import com.kakao_tech.community.dto.PostDTO.SummaryResponse;
import com.kakao_tech.community.entity.Post;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.exception.code.PostErrorCode;
import com.kakao_tech.community.exception.common.RestApiException;
import com.kakao_tech.community.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public PostDTO.ListResponse getPosts(Integer limit, Long offset) {
        // offset이 없으면 가장 최신 글부터 조회 (Long.MAX_VALUE)
        long safeOffset = (offset == null) ? Long.MAX_VALUE : offset;

        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<Post> posts = postRepository.findByIdLessThanOrderByIdDesc(safeOffset, pageable);

        // Entity -> DTO 변환
        List<SummaryResponse> summaryPosts = posts.stream()
                .map(SummaryResponse::from)
                .collect(Collectors.toList());

        long postsTotalCount = postRepository.count();
        Long lastPostId = summaryPosts.isEmpty() ? null : summaryPosts.get(summaryPosts.size() - 1).getId();

        return PostDTO.ListResponse.builder()
                .posts(summaryPosts)
                .postsTotalCount(postsTotalCount)
                .postsGetCount(summaryPosts.size())
                .lastPostId(lastPostId)
                .build();
    }

    @Transactional(readOnly = true)
    public PostDTO.DetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        return PostDTO.DetailResponse.from(post);
    }

    @Transactional
    public PostDTO.CreateResponse createPost(String title, String body, MultipartFile image, User user) {
        String imageUrl = null;

        // 이미지가 있으면 S3에 업로드
        if (image != null && !image.isEmpty()) {
            imageUrl = imageService.uploadPostImage(image);
        }

        Post post = new Post(title, body, imageUrl, user);
        postRepository.save(post);
        return new PostDTO.CreateResponse(post.getId());
    }

    @Transactional
    public PostDTO.UpdateResponse updatePost(Long postId, String title, String body, MultipartFile image, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RestApiException(AuthErrorCode.ACCESS_DENIED);
        }

        String imageUrl = post.getImageUrl(); // 기존 이미지 유지

        // 새 이미지가 있으면 S3에 업로드
        if (image != null && !image.isEmpty()) {
            imageUrl = imageService.uploadPostImage(image);
        }

        post.update(title, body, imageUrl);

        return new PostDTO.UpdateResponse(post.getId());
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RestApiException(AuthErrorCode.ACCESS_DENIED);
        }

        postRepository.delete(post);
    }
}
