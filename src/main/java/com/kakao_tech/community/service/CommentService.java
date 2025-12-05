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

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // TODO: 댓글들 불러오기
    // Sql문을 여러번 날리는것보단 한번에 SQL한방으로 그런느낌으로 ㅇㅋㅇㅋ
    // public String getComments(Integer limit, Long offset) {
    // }

    // 댓글 가져오기
    public CommentDTO.Response getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(CommentErrorCode.INVALID_COMMENT_ID));

        User user = comment.getUser();

        CommentDTO.Author author =
            CommentDTO.Author.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .build();

        CommentDTO.Response response =
            CommentDTO.Response.builder()
                .id(comment.getId())
                .author(author)
                .body(comment.getBody())
                .createAt(comment.getCreatedAt())
                .updateAt(comment.getUpdatedAt())
                .build();

        return response;
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
        comment = commentRepository.save(comment);

        CommentDTO.CreateResponse response = new CommentDTO.CreateResponse(comment.getId());
        return response;
    }

    // @Transient
    // public String deleteComment(User user, Comment comment) {
    // }
}
