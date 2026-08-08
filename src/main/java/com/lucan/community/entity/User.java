package com.lucan.community.entity;

import com.lucan.community.enums.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String nickname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Team favoriteTeam;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted = false;

    protected User() {
    }

    public User(String email, String password, String nickname, String profileImage, Team favoriteTeam) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.favoriteTeam = favoriteTeam;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deleted = true;
        this.nickname = "탈퇴 유저";
    }
    public boolean isDeleted() {
        return deleted;
    }

    public void update(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
