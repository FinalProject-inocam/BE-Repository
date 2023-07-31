package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentsRepository extends JpaRepository<Comments,Long> {
}
