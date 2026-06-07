package com.lucan.community.service;

import com.lucan.community.dto.like.LikeResponse;
import com.lucan.community.dto.post.*;
import com.lucan.community.dto.response.ApiResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    public ApiResponse getPosts() {
        List<PostListResponse> posts = List.of(new PostListResponse
                (
                        1,
                        "test_title",
                        10,
                        2,
                        90,
                        "lucan"
                )
        );
        return new ApiResponse("get_posts_success", posts);
    }

    public ApiResponse getPost(Integer postId) {
        if (postId != 1) {
            throw new IllegalArgumentException("post_not_found");
        }
        PostDetailResponse post = new PostDetailResponse(
                1,
                "test_title",
                "lucan",
                "image.png",
                "test_content",
                10,
                90,
                2,
                "test_comment"
        );
        return new ApiResponse("get_post_success", post);
    }

    public ApiResponse createPost(PostCreateRequest request) {

        PostCreateResponse response = new PostCreateResponse(1);

        return new ApiResponse("create_post_success", response);
    }

    public ApiResponse updatePost(Integer postId, PostUpdateRequest request) {
        if (postId != 1) {
            throw new IllegalArgumentException("post_not_found");
        }

        PostUpdateResponse response = new PostUpdateResponse(postId);

        return new ApiResponse("post_update_success", response);
    }

    public ApiResponse deletePost(Integer postId) {
        if (postId != 1) {
            throw new IllegalArgumentException("post_not_found");
        }

        return new ApiResponse("post_delete_success", null);
    }

    public ApiResponse createLike(Integer postId) {

        LikeResponse response = new LikeResponse(10);

        return new ApiResponse("like_success", response);
    }

    public ApiResponse deleteLike(Integer postId) {

        LikeResponse response = new LikeResponse(9);

        return new ApiResponse("unlike_success", response);
    }
}
