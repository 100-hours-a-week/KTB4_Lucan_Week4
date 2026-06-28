package com.lucan.community.repository;

import com.lucan.community.entity.Comment;
import com.lucan.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer countByPost(Post post);
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    void deleteByPost(Post post);
}
