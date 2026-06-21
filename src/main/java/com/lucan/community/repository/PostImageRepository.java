package com.lucan.community.repository;

import com.lucan.community.entity.Post;
import com.lucan.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPost(Post post);
}
