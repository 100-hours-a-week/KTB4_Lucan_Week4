package com.lucan.community.controller;

import com.lucan.community.dto.comment.CommentCreateRequest;
import com.lucan.community.dto.comment.CommentCreateResponse;
import com.lucan.community.dto.comment.CommentUpdateRequest;
import com.lucan.community.dto.comment.CommentUpdateResponse;
import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.message.MessageCode;
import com.lucan.community.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}/comments")
    public ApiResponse createComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest request) {
        CommentCreateResponse response = commentService.createComment(postId, request);

        return new ApiResponse(MessageCode.CREATE_COMMENT_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}/comments/{commentId}")
    public ApiResponse updateComment(@PathVariable Long postId, @PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequest request) {
        CommentUpdateResponse response = commentService.updateComment(postId, commentId, request);

        return new ApiResponse(MessageCode.COMMENT_UPDATE_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(postId, commentId);

        return new ApiResponse(MessageCode.COMMENT_DELETE_SUCCESS.getMessage(), null);
    }
}