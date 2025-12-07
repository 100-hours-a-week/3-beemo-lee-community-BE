package com.kakao_tech.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kakao_tech.community.entity.Like;
import com.kakao_tech.community.entity.Post;
import com.kakao_tech.community.entity.User;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    void deleteByPost(Post post);
    int countByPost(Post post);
}
