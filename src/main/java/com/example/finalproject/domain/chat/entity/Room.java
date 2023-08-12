package com.example.finalproject.domain.chat.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity

public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    private LocalDateTime createdTime;

    private LocalDateTime lastMessageTime;

    public Room() {
    }

    public Room(String roomName) {
        this.roomName = roomName;
        this.createdTime = LocalDateTime.now();
    }

    // Getter and Setter methods...
}