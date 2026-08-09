package com.lucan.community.service;

import com.lucan.community.dto.like.LikeResponse;
import com.lucan.community.dto.post.*;
import com.lucan.community.entity.*;
import com.lucan.community.enums.Team;
import com.lucan.community.exception.ConflictException;
import com.lucan.community.exception.NotFoundException;
import com.lucan.community.exception.UnauthorizedException;
import com.lucan.community.message.MessageCode;
import com.lucan.community.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public PostPageResponse getPosts(Team team, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostListResponse> postPage;

        if(team == null) {
            postPage = postRepository.findAllPostList(pageable);
        }
        else {
            postPage = postRepository.findPostListByTeam(team,pageable);
        }
        return new PostPageResponse(
                postPage.getContent(),
                postPage.getNumber(),
                postPage.getTotalPages(),
                postPage.getTotalElements(),
                postPage.isFirst(),
                postPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public List<PostPreviewResponse> getRecentPosts(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(MessageCode.USER_NOT_FOUND.getMessage()));

        Pageable pageable = PageRequest.of(0, 3);

        return postRepository.findRecentPostsByTeam(user.getFavoriteTeam(), pageable);
    }

    @Transactional(readOnly = true)
    public List<PostPreviewResponse> getPopularPosts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(MessageCode.USER_NOT_FOUND.getMessage()));

        Pageable pageable = PageRequest.of(0, 3);

        return postRepository.findPopularPostsByTeam(user.getFavoriteTeam(), pageable);
    }

    @Transactional
    public PostDetailResponse getPost(Long postId, Long userId) {
        Post post = findPost(postId);

        PostImage postImage = postImageRepository.findByPost(post).orElse(null);

        String image = null;

        if (postImage != null) {
            image = postImage.getImage();
        }

        Integer likeCount = postLikeRepository.countByPost(post);
        Integer commentCount = commentRepository.countByPost(post);

        boolean liked = postLikeRepository.existsByPost_PostIdAndUser_UserId(postId, userId);

        return new PostDetailResponse(
                post.getPostId(),
                post.getTitle(),
                post.getTeam(),
                post.getUser().getFavoriteTeam(),
                post.getUser().getNickname(),
                post.getUser().getProfileImage(),
                image,
                post.getContent(),
                likeCount,
                post.getViewCount(),
                commentCount,
                post.getCreatedAt(),
                post.getUpdatedAt(),
                liked
        );
    }

    @Transactional
    public void increaseViewCount(
            Long postId,
            String viewEventId,
            HttpSession session
    ) {
        String sessionKey =
                "processedViewEvent_" + viewEventId;

        synchronized (session) {
            if (session.getAttribute(sessionKey) != null) {
                return;
            }

            session.setAttribute(sessionKey, true);
        }

        Post post = findPost(postId);
        post.increaseViewCount();
    }

    @Transactional
    public PostCreateResponse createPost(Long userId, PostCreateRequest request) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        if (user.isDeleted()) {
            throw new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getTeam(),
                user
        );

        Post savedPost = postRepository.save(post);

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String imageUrl = s3Service.uploadImage(request.getImageFile(),"posts");

            PostImage postImage = new PostImage(imageUrl,savedPost);

            postImageRepository.save(postImage);
        }

        return new PostCreateResponse(savedPost.getPostId());
    }

    @Transactional
    public PostUpdateResponse updatePost(Long postId, Long userId, PostUpdateRequest request) {
        Post post = findPost(postId);

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException(MessageCode.POST_UPDATE_FORBIDDEN.getMessage());
        }

        String title = request.getTitle();
        String content = request.getContent();

        boolean hasTitle = title != null && !title.isBlank();

        boolean hasContent = content != null && !content.isBlank();

        boolean hasImage = request.getImageFile() != null && !request.getImageFile().isEmpty();

        if (!hasTitle && !hasContent && !hasImage) {
            throw new IllegalArgumentException(MessageCode.INVALID_REQUEST.getMessage());
        }

        if (hasTitle) {
            post.setTitle(title.trim());
        }

        if (hasContent) {
            post.setContent(content.trim());
        }

        if (hasImage) {
            PostImage existingImage = postImageRepository.findByPost(post).orElse(null);

            String newImageUrl = s3Service.uploadImage(request.getImageFile(), "posts");

            if (existingImage == null) {
                PostImage newPostImage = new PostImage(newImageUrl, post);

                postImageRepository.save(newPostImage);
            } else {
                String oldImageUrl = existingImage.getImage();

                existingImage.setImage(newImageUrl);

                if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                    s3Service.deleteImage(oldImageUrl);
                }
            }
        }

        post.updateModifiedAt();

        return new PostUpdateResponse(
                post.getPostId()
        );
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = findPost(postId);

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException(
                    MessageCode.POST_DELETE_FORBIDDEN.getMessage()
            );
        }

        PostImage postImage = postImageRepository.findByPost(post).orElse(null);

        if (postImage != null) {
            String imageUrl = postImage.getImage();

            if (imageUrl != null && !imageUrl.isBlank()) {
                s3Service.deleteImage(imageUrl);
            }
        }

        commentRepository.deleteByPost(post);
        postLikeRepository.deleteByPost(post);
        postImageRepository.deleteByPost(post);

        postRepository.delete(post);
    }

    @Transactional
    public LikeResponse createLike(Long postId, Long userId) {

        Post post = findPost(postId);

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        if (user.isDeleted()) {
            throw new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage());
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
    public LikeResponse deleteLike(Long postId, Long userId) {
        Post post = findPost(postId);

        User user = userRepository.findById(userId).orElse(null);

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