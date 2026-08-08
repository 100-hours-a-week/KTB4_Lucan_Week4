package com.lucan.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = {
            @UniqueConstraint(name = "UK_post_likes_user_post", columnNames = {"user_id", "post_id"})
        },
        indexes = {
            @Index(
                    name = "idx_post_likes_post_id",
                    columnList = "post_id"
            )
        }
)
@Getter @Setter
public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected PostLike() {
    }

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}