package com.lucan.community.repository;

import com.lucan.community.entity.Post;
import com.lucan.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    Optional<PostImage> findByPost(Post post);

    void deleteByPost(Post post);
}
