package com.example.finalproject.domain.chat.repository;

import com.example.finalproject.domain.chat.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}