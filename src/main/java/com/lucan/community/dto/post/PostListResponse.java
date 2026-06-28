package com.lucan.community.dto.post;

import lombok.Getter;

@Getter
public class PostListResponse {

    private Long postId;
    private String title;
    private Long likeCount;
    private Long commentCount;
    private Integer viewCount;
    private String nickname;

    public PostListResponse(Long postId, String title, Long likeCount, Long commentCount,
                            Integer viewCount, String nickname) {
        this.postId = postId;
        this.title = title;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.nickname = nickname;
    }
}
