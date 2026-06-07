package com.lucan.community.controller;

import com.lucan.community.dto.comment.CommentCreateRequest;
import com.lucan.community.dto.comment.CommentUpdateRequest;
import com.lucan.community.dto.response.ApiResponse;
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
    public ApiResponse createComment(@PathVariable Integer postId, @Valid @RequestBody CommentCreateRequest request) {
        return commentService.createComment(postId, request);
    }

    @PatchMapping("/{postId}/comments/{commentId}")
    public ApiResponse updateComment(@PathVariable Integer postId, @PathVariable Integer commentId, @Valid @RequestBody CommentUpdateRequest request) {
        return commentService.updateComment(postId, commentId, request);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse deleteComment(@PathVariable Integer postId, @PathVariable Integer commentId) {
        return commentService.deleteComment(postId, commentId);
    }
}
