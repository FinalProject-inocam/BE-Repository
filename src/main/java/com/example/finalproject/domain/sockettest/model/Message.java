package com.example.finalproject.domain.sockettest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message extends BaseModel {

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String room;

    @Column(nullable = false)
    private String username;


}
