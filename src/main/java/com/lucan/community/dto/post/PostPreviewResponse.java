package com.lucan.community.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostPreviewResponse {

    private Long postId;
    private String title;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
}
