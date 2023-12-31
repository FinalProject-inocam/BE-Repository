package com.example.finalproject.domain.sockettest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
// index를 통해서 쿼리 성능 향상이 가능하다는데...
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String roomName;
//    @Column
//    private String peer1;
//    @Column
//    private String peer2;
    @Column(nullable = false)
    private Integer status = 0;
    // 0 대기중, 1 진행중, 2 완료

    public Room(String roomName) {
        this.roomName = roomName;
        String[] peerList = roomName.split("!");
//        this.peer1 = peerList[0];
//        this.peer2 = peerList[1];
    }

    public void joinAdmin() {
        this.status = 1;
    }

    public void leaveAdmin() {
        this.status = 2;
    }

//    public void leavePeer(String username) {
//        this.peer1 = username.equals(peer1) ? null : peer1;
//        this.peer2 = username.equals(peer2) ? null : peer2;
//    }

//    public void rejoinPeer(String roomName) {
//        String[] peerList = roomName.split("!");
//        this.peer1 = peerList[0];
//        this.peer2 = peerList[1];
//    }
}
