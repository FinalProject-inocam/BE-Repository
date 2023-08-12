package com.example.finalproject.domain.chat.controller;

import com.example.finalproject.domain.chat.entity.Room;
import com.example.finalproject.domain.chat.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomRepository.save(room);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}