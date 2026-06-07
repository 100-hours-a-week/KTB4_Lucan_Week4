package com.lucan.community.dto.post;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class PostDetailResponse {

    private Integer postId;
    private String title;
    private String nickname;
    private String imageFile;
    private String content;
    private Integer likeCount;
    private Integer viewCount;
    private Integer commentCount;
    private String comment;
}