package com.lucan.community.service;

import com.lucan.community.dto.comment.CommentCreateRequest;
import com.lucan.community.dto.comment.CommentCreateResponse;
import com.lucan.community.dto.comment.CommentUpdateRequest;
import com.lucan.community.dto.comment.CommentUpdateResponse;
import com.lucan.community.dto.response.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public ApiResponse createComment(Integer postId, CommentCreateRequest request) {
        if (postId != 1) {
            throw new IllegalArgumentException("post_not_found");
        }

        CommentCreateResponse response = new CommentCreateResponse(1);

        return new ApiResponse("create_comment_success", response);
    }

    public ApiResponse updateComment(Integer postId, Integer commentId, CommentUpdateRequest request) {
        if (commentId != 1) {
            throw new IllegalArgumentException("comment_not_found");
        }

        CommentUpdateResponse response = new CommentUpdateResponse(commentId);

        return new ApiResponse("comment_update_success", response);
    }

    public ApiResponse deleteComment(Integer postId, Integer commentId) {
        if (commentId != 1) {
            throw new IllegalArgumentException("comment_not_found");
        }

        return new ApiResponse("comment_delete_success", null);
    }
}