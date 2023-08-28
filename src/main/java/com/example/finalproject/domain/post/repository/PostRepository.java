package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllBy(Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCase(String keyword,Pageable pageable);

    List<Post> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p JOIN FETCH p.postLikes pl GROUP BY p ORDER BY SIZE(pl) DESC LIMIT 5")
    List<Post> findAllByOrderByPostLikesSizeDesc();
}
