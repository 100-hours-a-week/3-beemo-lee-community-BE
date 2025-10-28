package com.kakao_tech.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @Column(unique = false, nullable = false, length = 64)
    private String sid;

    @Column(unique = false, nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(unique = false, nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime expiredAt;

    // 세션 무효화 하면 세션을 바로 삭제하는 방향으로 진행

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "INT")
    private User user;

    protected Session() {}

    public Session(String sid, User user) {
        this.sid = sid;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMinutes(30);

        this.user = user;
    }
}
