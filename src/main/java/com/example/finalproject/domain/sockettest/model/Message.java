package com.example.finalproject.domain.sockettest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

//    @Enumerated(EnumType.STRING)
//    private MessageType messageType;

    private String content;
    private String room;

    private String username;


}
