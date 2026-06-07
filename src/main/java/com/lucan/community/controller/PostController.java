package com.lucan.community.controller;

import com.lucan.community.dto.post.PostCreateRequest;
import com.lucan.community.dto.post.PostUpdateRequest;
import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return postService.getPosts();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public ApiResponse getPost(@PathVariable Integer postId) {
        return postService.getPost(postId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse createPost(@Valid @RequestBody PostCreateRequest request) {
        return postService.createPost(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{postId}")
    public ApiResponse updatePost(@PathVariable Integer postId, @Valid @RequestBody PostUpdateRequest request) {
        return postService.updatePost(postId, request);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse deletePost(@PathVariable Integer postId) {
        return postService.deletePost(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{postId}/likes")
    public ApiResponse createLike(@PathVariable Integer postId) {
        return postService.createLike(postId);
    }

    @DeleteMapping("/{postId}/likes")
    public ApiResponse deleteLike(@PathVariable Integer postId) {
        return postService.deleteLike(postId);
    }
}