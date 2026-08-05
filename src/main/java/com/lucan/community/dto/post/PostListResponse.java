package com.lucan.community.dto.post;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListResponse {

    private Long postId;
    private String title;
    private Long likeCount;
    private Long commentCount;
    private Integer viewCount;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostListResponse(Long postId, String title, Long likeCount, Long commentCount,
                            Integer viewCount, String nickname,String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.title = title;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
