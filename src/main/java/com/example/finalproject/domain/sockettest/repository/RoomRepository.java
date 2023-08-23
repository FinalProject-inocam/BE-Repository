package com.example.finalproject.domain.sockettest.repository;

import com.example.finalproject.domain.sockettest.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByRoomNameStartsWithOrRoomNameEndsWith(String usernameStart, String usernameEnd);
    List<Room> findAllByPeer1OrPeer2(String peer1, String peer2);
    List<Room> findAllByRoomNameStartsWith(String usernameStart);
    Optional<Room> findByRoomName(String roomName);
}
