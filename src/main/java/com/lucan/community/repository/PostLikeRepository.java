package com.lucan.community.repository;

import com.lucan.community.entity.Post;
import com.lucan.community.entity.PostLike;
import com.lucan.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    int countByPost(Post post);

    boolean existsByUserAndPost(User user, Post post);

    Optional<PostLike> findByUserAndPost(User user, Post post);
}
