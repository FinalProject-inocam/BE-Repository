package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<Image,Long> {
}
