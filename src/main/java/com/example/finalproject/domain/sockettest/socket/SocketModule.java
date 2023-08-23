package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Slf4j
public class SocketModule {


    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("sendMsg", Message.class, onChatReceived());
        // 테스트
        server.addEventListener("joinRoom", Message.class, joinRoomReceived());
        server.addEventListener("connected", Message.class, joinRoomReceived());

    }


    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.saveMessage(senderClient, data);
        };
    }

    private DataListener<Message> joinRoomReceived() {
        return (senderClient, data, ackSender) -> {
            String room = data.getRoom();
            log.info(room + "+" + data.getUsername());
            senderClient.joinRoom(room);
            socketService.requestPreviousChat(senderClient, room);
            log.info("previousChat 발송완료");
        };
    }

//    private DataListener<Message> connectSocket() {
//        return (senderClient, data, ackSender) -> {
//            senderClient.joinRoom("roomList");
//            socketService.requestPreviousChat(senderClient, room);
//            log.info("previousChat 발송완료");
//        };
//    }


    private ConnectListener onConnected() {
        return (client) -> {
//            String room = client.getHandshakeData().getSingleUrlParam("room");
//            String username = client.getHandshakeData().getSingleUrlParam("room");

            var params = client.getHandshakeData().getUrlParams();
            log.info("테스트 확인중 : " + params.toString());
            String room = params.get("room").stream().collect(Collectors.joining());
            String username = params.get("username").stream().collect(Collectors.joining());
            client.joinRoom(room);
//            socketService.saveInfoMessage(client, String.format(Constants.WELCOME_MESSAGE, username), room);
            socketService.requestPreviousChat(client, room);
            log.info("Socket ID[{}] - room[{}] - username [{}]  Connected to chat module through", client.getSessionId().toString(), room, username);
        };

    }

    private DisconnectListener onDisconnected() {
        return client -> {
            var params = client.getHandshakeData().getUrlParams();
            String room = params.get("room").stream().collect(Collectors.joining());
            String username = params.get("username").stream().collect(Collectors.joining());
//            socketService.saveInfoMessage(client, String.format(Constants.DISCONNECT_MESSAGE, username), room);
            log.info("Socket ID[{}] - room[{}] - username [{}]  discnnected to chat module through", client.getSessionId().toString(), room, username);
        };
    }


}
