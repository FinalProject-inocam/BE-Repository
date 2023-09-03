package com.example.finalproject.domain.sockettest.repository;

import com.example.finalproject.domain.sockettest.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByRoom(String room);

    Optional<Message> findTopByRoomOrderByCreatedAtDesc(String room);
}
