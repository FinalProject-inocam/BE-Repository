package com.example.finalproject.domain.chat.service;

import com.example.finalproject.domain.chat.entity.ChatRoom;
import com.example.finalproject.domain.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAll();
    }
}