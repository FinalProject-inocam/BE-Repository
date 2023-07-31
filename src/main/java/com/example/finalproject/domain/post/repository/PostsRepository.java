package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends JpaRepository <Posts,Long> {
}
