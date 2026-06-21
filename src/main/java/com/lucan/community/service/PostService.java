package com.lucan.community.service;

import com.lucan.community.dto.like.LikeRequest;
import com.lucan.community.dto.like.LikeResponse;
import com.lucan.community.dto.post.*;
import com.lucan.community.entity.*;
import com.lucan.community.exception.ConflictException;
import com.lucan.community.exception.NotFoundException;
import com.lucan.community.message.MessageCode;
import com.lucan.community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;

    public List<PostListResponse> getPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostListResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            Integer likeCount = postLikeRepository.countByPost(post);
            Integer commentCount = commentRepository.countByPost(post);

            PostListResponse response = new PostListResponse(
                    post.getPostId(),
                    post.getTitle(),
                    likeCount,
                    commentCount,
                    post.getViewCount(),
                    post.getUser().getNickname()
            );

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public PostDetailResponse getPost(Long postId) {
        Post post = findPost(postId);

        post.increaseViewCount();

        List<PostImage> images = postImageRepository.findByPost(post);
        String image = null;

        if (!images.isEmpty()) {
            image = images.get(0).getImage();
        }

        Comment commentEntity =
                commentRepository.findFirstByPost(post).orElse(null);

        String comment = null;

        if (commentEntity != null) {
            comment = commentEntity.getContent();
        }

        Integer likeCount = postLikeRepository.countByPost(post);
        Integer commentCount = commentRepository.countByPost(post);

        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getUser().getNickname(),
                image,
                post.getContent(),
                likeCount,
                post.getViewCount(),
                commentCount,
                comment
        );
    }

    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                user
        );

        Post savedPost = postRepository.save(post);

        if (request.getImageFile() != null) {
            PostImage postImage = new PostImage(
                    request.getImageFile(),
                    savedPost
            );

            postImageRepository.save(postImage);
        }

        return new PostCreateResponse(savedPost.getPostId());
    }

    @Transactional
    public PostUpdateResponse updatePost(Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return new PostUpdateResponse(post.getPostId());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findPost(postId);
        postRepository.delete(post);
    }

    @Transactional
    public LikeResponse createLike(Long postId, LikeRequest request) {
        Post post = findPost(postId);

        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new ConflictException(MessageCode.INVALID_REQUEST.getMessage());
        }

        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

        Integer likeCount = postLikeRepository.countByPost(post);

        return new LikeResponse(likeCount);
    }

    @Transactional
    public LikeResponse deleteLike(Long postId, LikeRequest request) {
        Post post = findPost(postId);

        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        PostLike postLike = postLikeRepository
                .findByUserAndPost(user, post)
                .orElse(null);

        if (postLike == null) {
            throw new NotFoundException(MessageCode.INVALID_REQUEST.getMessage());
        }

        postLikeRepository.delete(postLike);

        Integer likeCount = postLikeRepository.countByPost(post);

        return new LikeResponse(likeCount);
    }

    private Post findPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new NotFoundException(MessageCode.POST_NOT_FOUND.getMessage());
        }

        return post;
    }
}