package com.lucan.community.repository;

import com.lucan.community.dto.post.PostListResponse;
import com.lucan.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT p
    FROM Post p
    JOIN FETCH p.user
""")
    List<Post> findAllWithUser();

    @Query("""
SELECT new com.lucan.community.dto.post.PostListResponse(
    p.postId,
    p.title,
    COUNT(DISTINCT pl),
    COUNT(DISTINCT c),
    p.viewCount,
    p.user.nickname,
    p.createdAt,
    p.updatedAt
)
FROM Post p
LEFT JOIN PostLike pl ON pl.post = p
LEFT JOIN Comment c ON c.post = p
JOIN p.user u
GROUP BY p.postId, p.title, p.viewCount,
         p.user.nickname, p.createdAt, p.updatedAt
ORDER BY p.createdAt DESC
""")
    Page<PostListResponse> findPostList(Pageable pageable);

}
