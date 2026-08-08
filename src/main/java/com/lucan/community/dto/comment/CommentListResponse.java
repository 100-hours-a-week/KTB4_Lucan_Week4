package com.lucan.community.dto.comment;

import com.lucan.community.enums.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentListResponse {

    private Long commentId;
    private String content;
    private String nickname;
    private String profileImage;
    private Team favoriteTeam;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}