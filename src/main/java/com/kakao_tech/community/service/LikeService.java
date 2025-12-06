package com.kakao_tech.community.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao_tech.community.dto.LikeDTO;
import com.kakao_tech.community.entity.Like;
import com.kakao_tech.community.entity.Post;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.exception.code.PostErrorCode;
import com.kakao_tech.community.exception.common.RestApiException;
import com.kakao_tech.community.repository.LikeRepository;
import com.kakao_tech.community.repository.PostRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeDTO.AddResponse addLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        // 이미 좋아요를 했는지 확인
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new RestApiException(PostErrorCode.ALREADY_LIKED);
        }

        // 좋아요 생성
        Like like = new Like(user, post);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);

        // Post의 좋아요 개수 증가
        post.setLikesCnt(post.getLikesCnt() + 1);
        postRepository.save(post);

        return LikeDTO.AddResponse.of(post, "좋아요를 했어요.");
    }

    @Transactional
    public LikeDTO.RemoveResponse removeLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        // 좋아요가 없으면 예외 처리
        Optional<Like> like = likeRepository.findByUserAndPost(user, post);
        if (like.isEmpty()) {
            throw new RestApiException(PostErrorCode.LIKE_NOT_FOUND);
        }

        // 좋아요 삭제
        likeRepository.delete(like.get());

        // Post의 좋아요 개수 감소
        post.setLikesCnt(Math.max(0, post.getLikesCnt() - 1));
        postRepository.save(post);

        return LikeDTO.RemoveResponse.of(post, "좋아요를 취소했어요.");
    }

    @Transactional(readOnly = true)
    public LikeDTO.CheckResponse isLikedByUser(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        boolean isLiked = likeRepository.existsByUserAndPost(user, post);

        return LikeDTO.CheckResponse.of(post, isLiked);
    }
}
