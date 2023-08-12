package com.example.finalproject.domain.chat.entity;

import com.example.finalproject.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    private LocalDateTime timestamp;

    private Boolean isRead = false;

    public Message() {
    }

    // Constructor, Getter, and Setter methods...
}