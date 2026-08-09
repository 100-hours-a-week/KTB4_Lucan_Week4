package com.lucan.community.repository;

import com.lucan.community.dto.post.PostListResponse;
import com.lucan.community.dto.post.PostPreviewResponse;
import com.lucan.community.entity.Post;
import com.lucan.community.enums.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository
        extends JpaRepository<Post, Long> {

    @Query("""
        SELECT new com.lucan.community.dto.post.PostListResponse(
            p.postId,
            p.title,
            p.team,
            u.favoriteTeam,
            COUNT(DISTINCT pl),
            COUNT(DISTINCT c),
            p.viewCount,
            u.nickname,
            u.profileImage,
            p.createdAt,
            p.updatedAt
        )
        FROM Post p
        JOIN p.user u
        LEFT JOIN PostLike pl ON pl.post = p
        LEFT JOIN Comment c ON c.post = p
        GROUP BY
            p.postId,
            p.title,
            p.team,
            u.favoriteTeam,
            p.viewCount,
            u.nickname,
            u.profileImage,
            p.createdAt,
            p.updatedAt
        ORDER BY p.createdAt DESC
        """)
    Page<PostListResponse> findAllPostList(
            Pageable pageable
    );

    @Query("""
        SELECT new com.lucan.community.dto.post.PostListResponse(
            p.postId,
            p.title,
            p.team,
            u.favoriteTeam,
            COUNT(DISTINCT pl),
            COUNT(DISTINCT c),
            p.viewCount,
            u.nickname,
            u.profileImage,
            p.createdAt,
            p.updatedAt
        )
        FROM Post p
        JOIN p.user u
        LEFT JOIN PostLike pl ON pl.post = p
        LEFT JOIN Comment c ON c.post = p
        WHERE p.team = :team
        GROUP BY
            p.postId,
            p.title,
            p.team,
            u.favoriteTeam,
            p.viewCount,
            u.nickname,
            u.profileImage,
            p.createdAt,
            p.updatedAt
        ORDER BY p.createdAt DESC
        """)
    Page<PostListResponse> findPostListByTeam(
            Team team,
            Pageable pageable
    );

    @Query("""
        SELECT new com.lucan.community.dto.post.PostPreviewResponse(
            p.postId,
            p.title,
            COUNT(DISTINCT pl),
            COUNT(DISTINCT c),
            p.viewCount,
            p.createdAt
        )
        FROM Post p
        LEFT JOIN PostLike pl ON pl.post = p
        LEFT JOIN Comment c ON c.post = p
        WHERE p.team = :team
        GROUP BY
            p.postId,
            p.title,
            p.viewCount,
            p.createdAt
        ORDER BY p.createdAt DESC
        """)
    List<PostPreviewResponse> findRecentPostsByTeam(
            Team team,
            Pageable pageable
    );

    @Query("""
        SELECT new com.lucan.community.dto.post.PostPreviewResponse(
            p.postId,
            p.title,
            COUNT(DISTINCT pl),
            COUNT(DISTINCT c),
            p.viewCount,
            p.createdAt
        )
        FROM Post p
        LEFT JOIN PostLike pl ON pl.post = p
        LEFT JOIN Comment c ON c.post = p
        WHERE p.team = :team
        GROUP BY
            p.postId,
            p.title,
            p.viewCount,
            p.createdAt
        ORDER BY
            COUNT(DISTINCT pl) DESC,
            p.viewCount DESC,
            p.createdAt DESC
        """)
    List<PostPreviewResponse> findPopularPostsByTeam(
            Team team,
            Pageable pageable
    );
}