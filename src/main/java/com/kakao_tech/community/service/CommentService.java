package com.kakao_tech.community.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao_tech.community.dto.CommentDTO;
import com.kakao_tech.community.entity.Comment;
import com.kakao_tech.community.entity.Post;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.exception.code.AuthErrorCode;
import com.kakao_tech.community.exception.code.CommentErrorCode;
import com.kakao_tech.community.exception.code.PostErrorCode;
import com.kakao_tech.community.exception.common.RestApiException;
import com.kakao_tech.community.repository.CommentRepository;
import com.kakao_tech.community.repository.PostRepository;
import com.kakao_tech.community.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 댓글 목록 가져오기
    @Transactional(readOnly = true)
    public CommentDTO.ListResponse getComments(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);

        List<CommentDTO.Response> commentResponses = comments.stream()
                .map(CommentDTO.Response::from)
                .collect(Collectors.toList());

        return CommentDTO.ListResponse.builder()
                .comments(commentResponses)
                .commentsTotalCount((long) commentResponses.size())
                .commentsGetCount(commentResponses.size())
                .build();
    }

    // 댓글 가져오기
    @Transactional(readOnly = true)
    public CommentDTO.Response getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.INVALID_COMMENT_ID));

        return CommentDTO.Response.from(comment);
    }

    // 댓글 작성
    @Transactional
    public CommentDTO.CreateResponse createComment(String body, Integer userId, Long postId) {
        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(AuthErrorCode.REQUIRED_SIGN_IN));

        // Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(PostErrorCode.INVALID_POST_ID));

        // 댓글 생성 및 저장
        Comment comment = new Comment(body, user, post);
        commentRepository.save(comment);

        // Post의 댓글 수 증가
        post.incrementCommentCount();

        return new CommentDTO.CreateResponse(comment.getId());
    }

    // 댓글 수정
    @Transactional
    public CommentDTO.UpdateResponse updateComment(Long commentId, String body, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.INVALID_COMMENT_ID));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(AuthErrorCode.ACCESS_DENIED);
        }

        comment.update(body);
        // JPA Dirty Checking으로 save 호출 안해도 됨

        return CommentDTO.UpdateResponse.builder()
                .commentId(comment.getId())
                .body(comment.getBody())
                .build();
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.INVALID_COMMENT_ID));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(AuthErrorCode.ACCESS_DENIED);
        }

        // Post의 댓글 수 감소
        Post post = comment.getPost();
        post.decrementCommentCount();

        commentRepository.delete(comment);
    }
}
