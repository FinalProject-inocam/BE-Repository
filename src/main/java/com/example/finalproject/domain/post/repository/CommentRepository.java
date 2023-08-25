package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
