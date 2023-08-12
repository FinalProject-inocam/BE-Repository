package com.example.finalproject.domain.chat.repository;

import com.example.finalproject.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}