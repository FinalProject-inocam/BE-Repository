package com.example.finalproject.domain.sockettest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String room;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String memo;

    public Memo(String room, String memo) {
        this.room = room;
        this.memo = memo;
    }
}