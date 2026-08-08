package com.lucan.community.repository;

import com.lucan.community.dto.comment.CommentListResponse;
import com.lucan.community.entity.Comment;
import com.lucan.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countByPost(Post post);

    @Query("""
        SELECT new com.lucan.community.dto.comment.CommentListResponse(
            c.commentId,
            c.content,
            u.nickname,
            u.profileImage,
            u.favoriteTeam,
            c.createdAt,
            c.updatedAt
        )
        FROM Comment c
        JOIN c.user u
        WHERE c.post = :post
        ORDER BY c.createdAt DESC
        """)
    List<CommentListResponse> findCommentListByPost(
            Post post
    );

    void deleteByPost(Post post);
}
