package com.example.finalproject.domain.sockettest.repository;

import com.example.finalproject.domain.sockettest.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByRoomNameContainingAndStatus(String username, Integer status);
    Optional<Room> findByRoomName(String roomName);
}
