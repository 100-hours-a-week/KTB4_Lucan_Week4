package com.lucan.community.service;

import com.lucan.community.dto.comment.CommentCreateRequest;
import com.lucan.community.dto.comment.CommentCreateResponse;
import com.lucan.community.dto.comment.CommentUpdateRequest;
import com.lucan.community.dto.comment.CommentUpdateResponse;
import com.lucan.community.entity.Comment;
import com.lucan.community.entity.Post;
import com.lucan.community.entity.User;
import com.lucan.community.exception.NotFoundException;
import com.lucan.community.message.MessageCode;
import com.lucan.community.repository.CommentRepository;
import com.lucan.community.repository.PostRepository;
import com.lucan.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponse createComment(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new NotFoundException(MessageCode.POST_NOT_FOUND.getMessage());
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);

        if (user == null) {
            throw new NotFoundException(MessageCode.LOGIN_REQUIRED.getMessage());
        }

        Comment comment = new Comment(
                request.getContent(),
                user,
                post
        );

        Comment savedComment = commentRepository.save(comment);

        return new CommentCreateResponse(savedComment.getCommentId());
    }

    @Transactional
    public CommentUpdateResponse updateComment(Long postId, Long commentId, CommentUpdateRequest request) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new NotFoundException(MessageCode.POST_NOT_FOUND.getMessage());
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            throw new NotFoundException(MessageCode.COMMENT_NOT_FOUND.getMessage());
        }

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new NotFoundException(MessageCode.COMMENT_NOT_FOUND.getMessage());
        }

        comment.setContent(request.getContent());

        return new CommentUpdateResponse(comment.getCommentId());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            throw new NotFoundException(MessageCode.POST_NOT_FOUND.getMessage());
        }

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            throw new NotFoundException(MessageCode.COMMENT_NOT_FOUND.getMessage());
        }

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new NotFoundException(MessageCode.COMMENT_NOT_FOUND.getMessage());
        }

        commentRepository.delete(comment);
    }
}