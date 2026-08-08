package com.lucan.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.lucan.community.enums.Team;

@Entity
@Table(name = "posts",
        indexes = {
            @Index(name = "idx_posts_created_at",
                    columnList = "created_at"),
            @Index(name = "idx_posts_team_created_at",
            columnList = "team, created_at")
        })
@Getter @Setter
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Post() {
    }

    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.user = user;
    }

    public Post(String title, String content, Team team, User user) {
        this.title = title;
        this.content = content;
        this.team = team;
        this.viewCount = 0;
        this.user = user;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updateModifiedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }



}