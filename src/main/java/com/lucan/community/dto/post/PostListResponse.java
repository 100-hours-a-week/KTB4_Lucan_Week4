package com.lucan.community.dto.post;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class PostListResponse {

    private Long postId;
    private String title;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private String nickname;
}
