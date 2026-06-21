package com.lucan.community.controller;

import com.lucan.community.dto.like.LikeRequest;
import com.lucan.community.dto.like.LikeResponse;
import com.lucan.community.dto.post.*;
import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.message.MessageCode;
import com.lucan.community.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiResponse getPosts() {
        List<PostListResponse> response = postService.getPosts();
        return new ApiResponse(MessageCode.GET_POSTS_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public ApiResponse getPost(@PathVariable Long postId) {
        PostDetailResponse response = postService.getPost(postId);
        return new ApiResponse(MessageCode.GET_POST_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse createPost(@Valid @RequestBody PostCreateRequest request) {
        PostCreateResponse response = postService.createPost(request);
        return new ApiResponse(MessageCode.CREATE_POST_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}")
    public ApiResponse updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        PostUpdateResponse response = postService.updatePost(postId, request);
        return new ApiResponse(MessageCode.POST_UPDATE_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}")
    public ApiResponse deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return new ApiResponse(MessageCode.POST_DELETE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{postId}/likes")
    public ApiResponse createLike(
            @PathVariable Long postId,
            @RequestBody LikeRequest request
    ) {
        LikeResponse response = postService.createLike(postId, request);
        return new ApiResponse(MessageCode.LIKE_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}/likes")
    public ApiResponse deleteLike(
            @PathVariable Long postId,
            @RequestBody LikeRequest request
    ) {
        LikeResponse response = postService.deleteLike(postId, request);
        return new ApiResponse(MessageCode.UNLIKE_SUCCESS.getMessage(), response);
    }
}