package com.lucan.community.controller;

import com.lucan.community.dto.like.LikeResponse;
import com.lucan.community.dto.post.*;
import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.enums.Team;
import com.lucan.community.message.MessageCode;
import com.lucan.community.security.CustomUserDetails;
import com.lucan.community.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse getPosts(
            @RequestParam(required = false) Team team,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PostPageResponse response = postService.getPosts(team, page, size);
        return new ApiResponse(MessageCode.GET_POSTS_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/recent")
    public ApiResponse getRecentPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PostPreviewResponse> response = postService.getRecentPosts(userDetails.getUserId());
        return new ApiResponse(MessageCode.GET_POST_SUCCESS.getMessage(),response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/popular")
    public ApiResponse getPopularPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PostPreviewResponse> response = postService.getPopularPosts(userDetails.getUserId());

        return new ApiResponse(MessageCode.GET_POSTS_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public ApiResponse getPost(@PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        PostDetailResponse response = postService.getPost(postId, userDetails.getUserId());
        return new ApiResponse(MessageCode.GET_POST_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{postId}/views")
    public ApiResponse increaseViewCount(
            @PathVariable Long postId,
            @RequestHeader("X-View-Event-Id") String viewEventId,
            HttpSession session
    ) {
        postService.increaseViewCount(
                postId,
                viewEventId,
                session
        );

        return new ApiResponse(
                MessageCode.INCREASE_VIEW_COUNT_SUCCESS.getMessage(),
                null
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute PostCreateRequest request) {
        PostCreateResponse response = postService.createPost(userDetails.getUserId(), request);
        return new ApiResponse(MessageCode.CREATE_POST_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}")
    public ApiResponse updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @ModelAttribute PostUpdateRequest request
    ) {
        PostUpdateResponse response = postService.updatePost(postId, userDetails.getUserId(), request);
        return new ApiResponse(MessageCode.POST_UPDATE_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}")
    public ApiResponse deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUserId());
        return new ApiResponse(MessageCode.POST_DELETE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{postId}/likes")
    public ApiResponse createLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LikeResponse response = postService.createLike(postId, userDetails.getUserId());
        return new ApiResponse(MessageCode.LIKE_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{postId}/likes")
    public ApiResponse deleteLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        LikeResponse response = postService.deleteLike(postId, userDetails.getUserId());
        return new ApiResponse(MessageCode.UNLIKE_SUCCESS.getMessage(), response);
    }
}