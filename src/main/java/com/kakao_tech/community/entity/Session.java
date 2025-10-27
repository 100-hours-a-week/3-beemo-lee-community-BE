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
    private String sessionId;

    @Column(unique = false, nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Column(unique = false, nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "INT")
    private User user;

    protected Session() {}

    public Session(String sessionId, User user) {
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusMinutes(30);

        this.user = user;
    }
}
